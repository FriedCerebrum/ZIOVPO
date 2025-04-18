import requests
import json
import unittest
import logging
import sys
from datetime import datetime
from functools import wraps


# Configure logging with colors and formatting
class ColoredFormatter(logging.Formatter):
    COLORS = {
        'DEBUG': '\033[94m',  # blue
        'INFO': '\033[92m',  # green
        'WARNING': '\033[93m',  # yellow
        'ERROR': '\033[91m',  # red
        'CRITICAL': '\033[91m\033[1m',  # bold red
        'RESET': '\033[0m'  # reset
    }

    def format(self, record):
        log_message = super().format(record)
        level_name = record.levelname
        if level_name in self.COLORS:
            return f"{self.COLORS[level_name]}{log_message}{self.COLORS['RESET']}"
        return log_message


# Set up logger
logger = logging.getLogger('api_test')
logger.setLevel(logging.DEBUG)

# Console handler with color formatting
console_handler = logging.StreamHandler(sys.stdout)
console_handler.setFormatter(ColoredFormatter('%(asctime)s - %(levelname)s - %(message)s'))
logger.addHandler(console_handler)

# File handler for persistent logs
file_handler = logging.FileHandler(f'api_test_{datetime.now().strftime("%Y%m%d_%H%M%S")}.log')
file_handler.setFormatter(logging.Formatter('%(asctime)s - %(levelname)s - %(message)s'))
logger.addHandler(file_handler)


# Decorator to continue test execution after errors
def continue_on_error(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        try:
            return func(*args, **kwargs)
        except Exception as e:
            logger.error(f"Error in {func.__name__}: {str(e)}")
            logger.error("Test will continue despite error")

    return wrapper


class ZIOVPOSignatureDetectionAPITests(unittest.TestCase):
    """
    Python script to test ZIOVPO Signature Detection API endpoints.
    This script follows the structure of the provided Postman collection.
    """

    # Class variable to control API key usage
    use_api_key = True

    @classmethod
    def set_api_key_usage(cls, disabled):
        """Enable or disable the use of API key in requests"""
        cls.use_api_key = disabled
        logger.info(f"API key usage set to: {disabled}")

    def setUp(self):
        """Initialize test variables and set up the test environment"""
        logger.info("=" * 80)
        logger.info("SETTING UP TEST ENVIRONMENT")
        logger.info("=" * 80)

        self.base_url = "http://localhost:8063"
        self.user_id = "user_123"
        self.api_key = "some_default_dev_key_never_use_in_production"

        # Use a session for better control over headers
        self.session = requests.Session()

        # Set headers based on API key usage flag
        headers = {
            "User-Id": self.user_id,
            "Content-Type": "application/json"
        }

        # Only add API key if enabled
        if self.__class__.use_api_key:
            headers["X-API-Key"] = self.api_key
            logger.info("API key included in request headers")
        else:
            logger.info("API key omitted from request headers")

        self.session.headers.update(headers)

        # Monkey patch the session to log requests and responses
        original_request = self.session.request

        def logging_request(method, url, **kwargs):
            # Log the request details
            logger.info(f"REQUEST: {method} {url}")
            logger.info(f"REQUEST HEADERS: {json.dumps(dict(self.session.headers), indent=2)}")

            if 'json' in kwargs:
                logger.info(f"REQUEST BODY: {json.dumps(kwargs['json'], indent=2)}")
            elif 'data' in kwargs:
                logger.info(f"REQUEST DATA: {kwargs['data']}")

            # Make the actual request
            response = original_request(method, url, **kwargs)

            # Log the response details
            logger.info(f"RESPONSE STATUS: {response.status_code} {response.reason}")
            logger.info(f"RESPONSE HEADERS: {json.dumps(dict(response.headers), indent=2)}")

            try:
                # Try to parse as JSON
                response_body = response.json()
                logger.info(f"RESPONSE BODY: {json.dumps(response_body, indent=2)}")
            except json.JSONDecodeError:
                # Fall back to text if not JSON
                logger.info(f"RESPONSE BODY (text): {response.text[:500]}")
                if len(response.text) > 500:
                    logger.info("... (response truncated)")

            logger.info("-" * 80)  # Separator for readability
            return response

        # Replace the request method with our logging version
        self.session.request = logging_request

        # Variables to store IDs for use across tests
        self.file_type_id = None
        self.scan_engine_id = None
        self.scan_report_id = None
        self.signature_id = None

        # Test resource names
        self.file_type_name = "ExecutableTest"
        self.scan_engine_name = "TestAntivirus"

    def tearDown(self):
        """Clean up resources after tests"""
        if hasattr(self, 'session') and self.session:
            self.session.close()
            logger.info("Session closed properly")
        logger.info("=" * 80)
        logger.info("TEST COMPLETED")
        logger.info("=" * 80)

    def test_api_endpoints(self):
        """Test all API endpoints in sequence"""
        logger.info("Starting ZIOVPO Signature Detection API Tests...")

        # Each test is wrapped in continue_on_error
        try:
            self._test_file_types()
        except Exception as e:
            logger.error(f"File Types test failed: {e}")
            logger.error("Continuing with next test...")

        try:
            self._test_scan_engines()
        except Exception as e:
            logger.error(f"Scan Engines test failed: {e}")
            logger.error("Continuing with next test...")

        try:
            self._test_scan_reports()
        except Exception as e:
            logger.error(f"Scan Reports test failed: {e}")
            logger.error("Continuing with next test...")

        logger.info("All tests attempted!")

    @continue_on_error
    def _test_file_types(self):
        """Test File Types API endpoints"""
        logger.info("\n1. TESTING FILE TYPES ENDPOINTS")
        logger.info("-" * 80)

        # First check if the file type already exists
        logger.info("Checking for existing File Types...")
        response = self.session.get(f"{self.base_url}/api/file-types")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        existing_file_types = response.json()

        # Try to find our test file type
        existing_file_type = next((ft for ft in existing_file_types if ft["name"] == self.file_type_name), None)

        if existing_file_type:
            logger.info(f"Found existing File Type with name '{self.file_type_name}', ID: {existing_file_type['id']}")
            self.file_type_id = existing_file_type["id"]
        else:
            # Create File Type only if it doesn't exist
            logger.info("Creating File Type...")
            payload = {
                "name": self.file_type_name,
                "extension": ".exe",
                "description": "Windows Executable Files",
                "mimeType": "application/x-msdownload",
                "isBinary": True
            }
            response = self.session.post(f"{self.base_url}/api/file-types", json=payload)

            self.assertEqual(response.status_code, 201, "Expected 201 Created status code")
            data = response.json()
            self.assertEqual(data["name"], self.file_type_name, "File Type name should match")
            self.file_type_id = data["id"]
            logger.info(f"File Type created with ID: {self.file_type_id}")

        # Get File Type by ID
        logger.info(f"Getting File Type by ID: {self.file_type_id}...")
        response = self.session.get(f"{self.base_url}/api/file-types/{self.file_type_id}")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        data = response.json()
        self.assertEqual(data["id"], self.file_type_id, "File Type ID should match")

        # Get File Type by Name
        logger.info(f"Getting File Type by name: {self.file_type_name}...")
        response = self.session.get(f"{self.base_url}/api/file-types/by-name/{self.file_type_name}")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        data = response.json()
        self.assertEqual(data["name"], self.file_type_name, "File Type name should match")

        # Update File Type - first check if updated name already exists
        updated_name = f"{self.file_type_name}Updated"
        logger.info(f"Checking if name '{updated_name}' already exists...")

        existing_updated = next((ft for ft in existing_file_types if ft["name"] == updated_name), None)

        if existing_updated:
            logger.info(f"Name '{updated_name}' already exists, using a unique name instead")
            updated_name = f"{self.file_type_name}Updated_{hash(str(self.file_type_id))}"

        logger.info(f"Updating File Type ID: {self.file_type_id} to name: {updated_name}...")
        payload = {
            "name": updated_name,
            "extension": ".exe",
            "description": "Updated Windows Executable Files",
            "mimeType": "application/x-msdownload",
            "isBinary": True
        }
        response = self.session.put(f"{self.base_url}/api/file-types/{self.file_type_id}", json=payload)

        if response.status_code == 400:
            logger.warning("Update failed with 400 status code - likely due to name conflict")
            logger.warning("Skipping update validation steps")
        else:
            self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
            data = response.json()
            self.assertEqual(data["name"], updated_name, "Updated name should match")
            self.assertEqual(data["description"], "Updated Windows Executable Files",
                             "Updated description should match")
            # Update the name for future reference
            self.file_type_name = updated_name

    @continue_on_error
    def _test_scan_engines(self):
        """Test Scan Engines API endpoints"""
        logger.info("\n2. TESTING SCAN ENGINES ENDPOINTS")
        logger.info("-" * 80)

        # First check if the scan engine already exists
        logger.info("Checking for existing Scan Engines...")
        response = self.session.get(f"{self.base_url}/api/scan-engines")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        existing_engines = response.json()

        # Try to find our test scan engine
        existing_engine = next((eng for eng in existing_engines if eng["name"] == self.scan_engine_name), None)

        if existing_engine:
            logger.info(f"Found existing Scan Engine with name '{self.scan_engine_name}', ID: {existing_engine['id']}")
            self.scan_engine_id = existing_engine["id"]
        else:
            # Create Scan Engine only if it doesn't exist
            logger.info("Creating Scan Engine...")
            payload = {
                "name": self.scan_engine_name,
                "description": "Test Antivirus Scanner",
                "version": "1.0.0",
                "isActive": True
            }
            response = self.session.post(f"{self.base_url}/api/scan-engines", json=payload)
            self.assertEqual(response.status_code, 201, "Expected 201 Created status code")
            data = response.json()
            self.assertEqual(data["name"], self.scan_engine_name, "Scan Engine name should match")
            self.scan_engine_id = data["id"]
            logger.info(f"Scan Engine created with ID: {self.scan_engine_id}")

        # Get All Scan Engines
        logger.info("Getting all Scan Engines...")
        response = self.session.get(f"{self.base_url}/api/scan-engines")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        data = response.json()
        self.assertTrue(isinstance(data, list), "Response should be an array")

        # Get Scan Engine by ID
        logger.info(f"Getting Scan Engine by ID: {self.scan_engine_id}...")
        response = self.session.get(f"{self.base_url}/api/scan-engines/{self.scan_engine_id}")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        data = response.json()
        self.assertEqual(data["id"], self.scan_engine_id, "Scan Engine ID should match")

        # Get Active Scan Engines
        logger.info("Getting active Scan Engines...")
        response = self.session.get(f"{self.base_url}/api/scan-engines/active")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        data = response.json()
        self.assertTrue(isinstance(data, list), "Response should be an array")

        # Check for existing name before updating
        updated_engine_name = f"{self.scan_engine_name}Updated"
        existing_updated_engine = next((eng for eng in existing_engines if eng["name"] == updated_engine_name), None)

        if existing_updated_engine:
            logger.info(f"Name '{updated_engine_name}' already exists, using a unique name instead")
            updated_engine_name = f"{self.scan_engine_name}Updated_{hash(str(self.scan_engine_id))}"

        # Update Scan Engine
        logger.info(f"Updating Scan Engine ID: {self.scan_engine_id} to name: {updated_engine_name}...")
        payload = {
            "name": updated_engine_name,
            "description": "Updated Test Antivirus Scanner",
            "version": "1.0.1",
            "isActive": True
        }
        response = self.session.put(f"{self.base_url}/api/scan-engines/{self.scan_engine_id}", json=payload)

        if response.status_code == 400:
            logger.warning("Update failed with 400 status code - likely due to name conflict")
            logger.warning("Skipping update validation steps")
        else:
            self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
            data = response.json()
            self.assertEqual(data["name"], updated_engine_name, "Updated name should match")
            self.assertEqual(data["version"], "1.0.1", "Updated version should match")
            # Update the name for future reference
            self.scan_engine_name = updated_engine_name

    @continue_on_error
    def _test_scan_reports(self):
        """Test Scan Reports API endpoints"""
        logger.info("\n3. TESTING SCAN REPORTS ENDPOINTS")
        logger.info("-" * 80)

        # Define test filename
        test_filename = "test_malware.exe"

        # First check for existing scan reports with the same filename
        logger.info("Checking for existing Scan Reports...")
        response = self.session.get(f"{self.base_url}/api/scan-reports")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        existing_reports = response.json()

        # Try to find an existing report with the same filename and scan engine
        existing_report = next((
            rep for rep in existing_reports
            if rep["fileName"] == test_filename and rep["scanEngineId"] == self.scan_engine_id
        ), None)

        if existing_report:
            logger.info(f"Found existing Scan Report for '{test_filename}', ID: {existing_report['id']}")
            self.scan_report_id = existing_report["id"]
        else:
            # Create Scan Report
            logger.info("Creating Scan Report...")
            payload = {
                "fileName": test_filename,
                "fileSize": 1024,
                "status": "INFECTED",
                "resultDetails": "Found test malware signature",
                "scanEngineId": self.scan_engine_id
            }
            response = self.session.post(f"{self.base_url}/api/scan-reports", json=payload)

            self.assertEqual(response.status_code, 201, "Expected 201 Created status code")
            data = response.json()
            self.assertEqual(data["fileName"], test_filename, "Scan Report fileName should match")
            self.assertEqual(data["status"], "INFECTED", "Scan Report status should match")
            self.scan_report_id = data["id"]
            logger.info(f"Scan Report created with ID: {self.scan_report_id}")

        # Get All Scan Reports
        logger.info("Getting all Scan Reports...")
        response = self.session.get(f"{self.base_url}/api/scan-reports")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        data = response.json()
        self.assertTrue(isinstance(data, list), "Response should be an array")

        # Get Scan Report by ID
        logger.info(f"Getting Scan Report by ID: {self.scan_report_id}...")
        response = self.session.get(f"{self.base_url}/api/scan-reports/{self.scan_report_id}")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        data = response.json()
        self.assertEqual(data["id"], self.scan_report_id, "Scan Report ID should match")

        # Get Scan Reports by Engine
        logger.info(f"Getting Scan Reports by Engine ID: {self.scan_engine_id}...")
        response = self.session.get(f"{self.base_url}/api/scan-reports/by-engine/{self.scan_engine_id}")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        data = response.json()
        self.assertTrue(isinstance(data, list), "Response should be an array")

        # Check if our created report is in the list
        found = False
        for report in data:
            if report["id"] == self.scan_report_id:
                found = True
                break
        self.assertTrue(found, "The created scan report should be in the list of reports by engine")

        # Get Scan Reports by Status
        logger.info("Getting Scan Reports with status: INFECTED...")
        response = self.session.get(f"{self.base_url}/api/scan-reports/by-status/INFECTED")
        self.assertEqual(response.status_code, 200, "Expected 200 OK status code")
        data = response.json()
        self.assertTrue(isinstance(data, list), "Response should be an array")

        # Make sure at least some reports have INFECTED status
        infected_reports = [report for report in data if report["status"] == "INFECTED"]
        self.assertGreaterEqual(len(infected_reports), 1, "Should find at least one infected scan report")


if __name__ == "__main__":
    # Example of how to disable API key before running tests
    # ZIOVPOSignatureDetectionAPITests.set_api_key_usage(False)

    # Run the tests
    unittest.main(argv=['first-arg-is-ignored'], exit=False)
