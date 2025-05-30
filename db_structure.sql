--
-- PostgreSQL database dump
--

-- Dumped from database version 15.2
-- Dumped by pg_dump version 15.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: file_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.file_types (
    id bigint NOT NULL,
    description character varying(500),
    extension character varying(255) NOT NULL,
    is_binary boolean,
    mime_type character varying(255),
    name character varying(255) NOT NULL
);


ALTER TABLE public.file_types OWNER TO postgres;

--
-- Name: file_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.file_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_types_id_seq OWNER TO postgres;

--
-- Name: file_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.file_types_id_seq OWNED BY public.file_types.id;


--
-- Name: filetype_scanengine; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.filetype_scanengine (
    filetype_id bigint NOT NULL,
    scanengine_id bigint NOT NULL
);


ALTER TABLE public.filetype_scanengine OWNER TO postgres;

--
-- Name: scan_engines; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.scan_engines (
    id bigint NOT NULL,
    description character varying(500),
    is_active boolean,
    name character varying(255) NOT NULL,
    version character varying(255)
);


ALTER TABLE public.scan_engines OWNER TO postgres;

--
-- Name: scan_engines_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.scan_engines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.scan_engines_id_seq OWNER TO postgres;

--
-- Name: scan_engines_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.scan_engines_id_seq OWNED BY public.scan_engines.id;


--
-- Name: scan_reports; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.scan_reports (
    id bigint NOT NULL,
    file_name character varying(255) NOT NULL,
    file_size bigint,
    result_details character varying(2000),
    scan_date timestamp(6) without time zone,
    status character varying(255) NOT NULL,
    scan_engine_id bigint NOT NULL,
    detected_signature_id uuid
);


ALTER TABLE public.scan_reports OWNER TO postgres;

--
-- Name: scan_reports_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.scan_reports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.scan_reports_id_seq OWNER TO postgres;

--
-- Name: scan_reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.scan_reports_id_seq OWNED BY public.scan_reports.id;


--
-- Name: signature_audit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.signature_audit (
    audit_id bigint NOT NULL,
    change_type character varying(255) NOT NULL,
    changed_at timestamp(6) without time zone NOT NULL,
    changed_by character varying(255) NOT NULL,
    fields_changed text,
    signature_id uuid NOT NULL
);


ALTER TABLE public.signature_audit OWNER TO postgres;

--
-- Name: signature_audit_audit_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.signature_audit_audit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.signature_audit_audit_id_seq OWNER TO postgres;

--
-- Name: signature_audit_audit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.signature_audit_audit_id_seq OWNED BY public.signature_audit.audit_id;


--
-- Name: signature_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.signature_history (
    history_id bigint NOT NULL,
    digital_signature text NOT NULL,
    file_type_id bigint NOT NULL,
    first_8_bytes character varying(255) NOT NULL,
    offset_end bigint NOT NULL,
    remainder_hash character varying(255) NOT NULL,
    remainder_length integer NOT NULL,
    signature_id uuid NOT NULL,
    status character varying(255) NOT NULL,
    threat_name character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version_created_at timestamp(6) without time zone NOT NULL,
    start_offset integer NOT NULL
);


ALTER TABLE public.signature_history OWNER TO postgres;

--
-- Name: signature_history_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.signature_history_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.signature_history_history_id_seq OWNER TO postgres;

--
-- Name: signature_history_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.signature_history_history_id_seq OWNED BY public.signature_history.history_id;


--
-- Name: signatures; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.signatures (
    first_8_bytes character varying(255) NOT NULL,
    object_name character varying(255) NOT NULL,
    remainder_hash character varying(255) NOT NULL,
    remainder_length integer NOT NULL,
    start_offset integer NOT NULL,
    file_type_id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    digital_signature text NOT NULL,
    offset_end bigint NOT NULL,
    status character varying(255) NOT NULL,
    threat_name character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid,
    version bigint
);


ALTER TABLE public.signatures OWNER TO postgres;

--
-- Name: file_types id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file_types ALTER COLUMN id SET DEFAULT nextval('public.file_types_id_seq'::regclass);


--
-- Name: scan_engines id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.scan_engines ALTER COLUMN id SET DEFAULT nextval('public.scan_engines_id_seq'::regclass);


--
-- Name: scan_reports id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.scan_reports ALTER COLUMN id SET DEFAULT nextval('public.scan_reports_id_seq'::regclass);


--
-- Name: signature_audit audit_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.signature_audit ALTER COLUMN audit_id SET DEFAULT nextval('public.signature_audit_audit_id_seq'::regclass);


--
-- Name: signature_history history_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.signature_history ALTER COLUMN history_id SET DEFAULT nextval('public.signature_history_history_id_seq'::regclass);


--
-- Name: file_types file_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file_types
    ADD CONSTRAINT file_types_pkey PRIMARY KEY (id);


--
-- Name: filetype_scanengine filetype_scanengine_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.filetype_scanengine
    ADD CONSTRAINT filetype_scanengine_pkey PRIMARY KEY (filetype_id, scanengine_id);


--
-- Name: scan_engines scan_engines_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.scan_engines
    ADD CONSTRAINT scan_engines_pkey PRIMARY KEY (id);


--
-- Name: scan_reports scan_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.scan_reports
    ADD CONSTRAINT scan_reports_pkey PRIMARY KEY (id);


--
-- Name: signature_audit signature_audit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.signature_audit
    ADD CONSTRAINT signature_audit_pkey PRIMARY KEY (audit_id);


--
-- Name: signature_history signature_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.signature_history
    ADD CONSTRAINT signature_history_pkey PRIMARY KEY (history_id);


--
-- Name: scan_engines uk_o1mdbgc5pusrt49w543dfsmkq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.scan_engines
    ADD CONSTRAINT uk_o1mdbgc5pusrt49w543dfsmkq UNIQUE (name);


--
-- Name: file_types uk_t3yaljpog74ubgem58ttlyx7d; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file_types
    ADD CONSTRAINT uk_t3yaljpog74ubgem58ttlyx7d UNIQUE (name);


--
-- Name: signatures fk3vb1dpy3fr0lyon4bt7tkauhj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.signatures
    ADD CONSTRAINT fk3vb1dpy3fr0lyon4bt7tkauhj FOREIGN KEY (file_type_id) REFERENCES public.file_types(id);


--
-- Name: filetype_scanengine fkc50foif2osx1ty8erdiwnqvqs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.filetype_scanengine
    ADD CONSTRAINT fkc50foif2osx1ty8erdiwnqvqs FOREIGN KEY (scanengine_id) REFERENCES public.scan_engines(id);


--
-- Name: filetype_scanengine fkf1119qr79mwc2go4er95v4wut; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.filetype_scanengine
    ADD CONSTRAINT fkf1119qr79mwc2go4er95v4wut FOREIGN KEY (filetype_id) REFERENCES public.file_types(id);


--
-- Name: scan_reports fkjakd78nog3c2u7j2kc4x7153b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.scan_reports
    ADD CONSTRAINT fkjakd78nog3c2u7j2kc4x7153b FOREIGN KEY (scan_engine_id) REFERENCES public.scan_engines(id);


--
-- PostgreSQL database dump complete
--

