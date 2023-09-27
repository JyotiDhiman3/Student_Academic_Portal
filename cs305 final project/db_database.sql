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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: acad_auth; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.acad_auth (
    username character varying(100),
    password character varying(100)
);


ALTER TABLE public.acad_auth OWNER TO postgres;

--
-- Name: auth_instructor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.auth_instructor (
    instructor_id character varying(320),
    password character varying(100)
);


ALTER TABLE public.auth_instructor OWNER TO postgres;

--
-- Name: authentication; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.authentication (
    id integer NOT NULL,
    email_id character varying(320),
    password character varying(72)
);


ALTER TABLE public.authentication OWNER TO postgres;

--
-- Name: authentication_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.authentication_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authentication_id_seq OWNER TO postgres;

--
-- Name: authentication_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.authentication_id_seq OWNED BY public.authentication.id;


--
-- Name: btp_capstone; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.btp_capstone (
    email_id character varying(320),
    instructor_id character varying(320),
    semester integer,
    project_title character varying(160),
    grade character varying(100)
);


ALTER TABLE public.btp_capstone OWNER TO postgres;

--
-- Name: course_completion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.course_completion (
    email_id character varying(120) NOT NULL,
    course_code character varying(10) NOT NULL,
    course_type character varying(40) NOT NULL,
    completed boolean NOT NULL,
    grade character varying(2)
);


ALTER TABLE public.course_completion OWNER TO postgres;

--
-- Name: instructor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.instructor (
    instructor_id character varying(320) NOT NULL,
    name character(72),
    course_code_offered character varying(10),
    cgpa_criteria text,
    sem integer,
    course_type character varying(100),
    year_for_course_offered character varying(100)
);


ALTER TABLE public.instructor OWNER TO postgres;

--
-- Name: program_cores; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.program_cores (
    course_code character varying(12),
    branch character(50),
    batch integer
);


ALTER TABLE public.program_cores OWNER TO postgres;

--
-- Name: program_electives; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.program_electives (
    course_code character varying(20),
    branch character(100),
    batch integer
);


ALTER TABLE public.program_electives OWNER TO postgres;

--
-- Name: record_of_grades; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.record_of_grades (
    email_id character varying(100),
    course_code character varying(10),
    grade character varying(3),
    semester integer
);


ALTER TABLE public.record_of_grades OWNER TO postgres;

--
-- Name: student_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.student_data (
    email_id character varying(320) NOT NULL,
    name character(72),
    sgpa numeric(10,2),
    semester integer,
    cgpa numeric(10,2)
);


ALTER TABLE public.student_data OWNER TO postgres;

--
-- Name: student_profile; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.student_profile (
    email_id character varying(320) NOT NULL,
    name character(72),
    contact_details character varying(20),
    batch character varying(12),
    joining_date text,
    cgpa numeric(10,2)
);


ALTER TABLE public.student_profile OWNER TO postgres;

--
-- Name: students; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.students (
    email_id character varying(100),
    total_credits integer
);


ALTER TABLE public.students OWNER TO postgres;

--
-- Name: authentication id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authentication ALTER COLUMN id SET DEFAULT nextval('public.authentication_id_seq'::regclass);


--
-- Data for Name: acad_auth; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.acad_auth (username, password) FROM stdin;
Staff Dean's office	Staff Dean
\.


--
-- Data for Name: auth_instructor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.auth_instructor (instructor_id, password) FROM stdin;
sodhi@iitrpr.ac.in	sodhi
bathula@iitrpr.ac.in	bathula
javed@iitrpr.ac.in	javed
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
valid.email@domain.com	\N
\.


--
-- Data for Name: authentication; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.authentication (id, email_id, password) FROM stdin;
1	2020csb1092@iitrpr.ac.in	2020csb1092
\.


--
-- Data for Name: btp_capstone; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.btp_capstone (email_id, instructor_id, semester, project_title, grade) FROM stdin;
\.


--
-- Data for Name: course_completion; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.course_completion (email_id, course_code, course_type, completed, grade) FROM stdin;
student1@example.com	CS101	Program core	t	A
student1@example.com	CS102	Program elective	t	B
student1@example.com	CS103	Program elective	t	C
student1@example.com	CS201	Program elective	t	D
student1@example.com	CP401	BTP Capstone	t	C
student2@example.com	CS101	Program core	t	A
student2@example.com	CS102	Program elective	t	B
student2@example.com	CS201	Program elective	t	C
student2@example.com	CS202	Program elective	t	D
student2@example.com	CP401	BTP Capstone	t	D
\.


--
-- Data for Name: instructor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.instructor (instructor_id, name, course_code_offered, cgpa_criteria, sem, course_type, year_for_course_offered) FROM stdin;
bathula@iitrpr.ac.in	Deepti Bathula                                                          	CS507	>=7.0	5	Program Core	2020 batch
javed@iitrpr.ac.in	Javed N Agrewala                                                        	BM403	>=6.0	4	Program Elective	All batches
johndoe@example.com	\N	COMP101	\N	1	\N	\N
\.


--
-- Data for Name: program_cores; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.program_cores (course_code, branch, batch) FROM stdin;
\.


--
-- Data for Name: program_electives; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.program_electives (course_code, branch, batch) FROM stdin;
\.


--
-- Data for Name: record_of_grades; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.record_of_grades (email_id, course_code, grade, semester) FROM stdin;
test_student@example.com	CS101	A	1
test_student@example.com	CS102	B+	2
\.


--
-- Data for Name: student_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.student_data (email_id, name, sgpa, semester, cgpa) FROM stdin;
\.


--
-- Data for Name: student_profile; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.student_profile (email_id, name, contact_details, batch, joining_date, cgpa) FROM stdin;
2020csb1062@iitrpr.ac.in	Dave                                                                    	34890-09860	2021	2021-10-15	8.50
2020eeb1100@iitrpr.ac.in	Ivan                                                                    	96345-76245	2019	2019-10-16	7.20
2020csb1092@iitrpr.ac.in	Jenna                                                                   	12345-67890	2020	2020-10-15	7.80
\.


--
-- Data for Name: students; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.students (email_id, total_credits) FROM stdin;
test@example.com	18
\.


--
-- Name: authentication_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.authentication_id_seq', 1, true);


--
-- Name: authentication authentication_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authentication
    ADD CONSTRAINT authentication_pkey PRIMARY KEY (id);


--
-- Name: course_completion course_completion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_completion
    ADD CONSTRAINT course_completion_pkey PRIMARY KEY (email_id, course_code);


--
-- Name: instructor instructor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.instructor
    ADD CONSTRAINT instructor_pkey PRIMARY KEY (instructor_id);


--
-- Name: student_profile stud; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student_profile
    ADD CONSTRAINT stud PRIMARY KEY (email_id);


--
-- Name: student_data student_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student_data
    ADD CONSTRAINT student_data_pkey PRIMARY KEY (email_id);


--
-- PostgreSQL database dump complete
--

