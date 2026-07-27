-- Baseline migration: snapshot of the schema as created by Hibernate's ddl-auto=update
-- before Flyway was introduced. This file exists only so Flyway has a V1 to baseline
-- against (spring.flyway.baseline-on-migrate=true, spring.flyway.baseline-version=1) —
-- it is never actually executed against the real database, since baselining marks
-- schema_version as already applied up to V1 without running the SQL.

CREATE TABLE public.availabilities (
    id uuid NOT NULL,
    active boolean NOT NULL,
    day_of_week integer NOT NULL,
    end_time time(6) without time zone NOT NULL,
    start_time time(6) without time zone NOT NULL,
    provider_id uuid NOT NULL
);

CREATE TABLE public.availability_blocks (
    id uuid NOT NULL,
    end_date_time timestamp(6) without time zone NOT NULL,
    reason character varying(255),
    start_date_time timestamp(6) without time zone NOT NULL,
    provider_id uuid NOT NULL
);

CREATE TABLE public.profiles (
    id uuid NOT NULL,
    avatar character varying(255),
    bio text,
    created_at timestamp(6) without time zone,
    name character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    user_id uuid NOT NULL
);

CREATE TABLE public.refresh_tokens (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    expiry_date timestamp(6) without time zone NOT NULL,
    token character varying(255) NOT NULL,
    user_id uuid NOT NULL
);

CREATE TABLE public.roles (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    name character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    CONSTRAINT roles_name_check CHECK (((name)::text = ANY ((ARRAY['ROLE_CLIENT'::character varying, 'ROLE_PROVIDER'::character varying])::text[])))
);

CREATE TABLE public.schedules (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    end_date_time timestamp(6) without time zone NOT NULL,
    price numeric(38,2) NOT NULL,
    start_date_time timestamp(6) without time zone NOT NULL,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    client_id uuid NOT NULL,
    provider_id uuid NOT NULL,
    service_id uuid NOT NULL,
    CONSTRAINT schedules_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'CONFIRMED'::character varying, 'CANCELLED'::character varying, 'COMPLETED'::character varying])::text[])))
);

CREATE TABLE public.services_offered (
    id uuid NOT NULL,
    active boolean NOT NULL,
    created_at timestamp(6) without time zone,
    description text,
    duration_minutes integer NOT NULL,
    name character varying(255) NOT NULL,
    price numeric(38,2) NOT NULL,
    updated_at timestamp(6) without time zone,
    provider_id uuid NOT NULL
);

CREATE TABLE public.users (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    role_id uuid NOT NULL
);

ALTER TABLE ONLY public.availabilities
    ADD CONSTRAINT availabilities_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.availability_blocks
    ADD CONSTRAINT availability_blocks_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.profiles
    ADD CONSTRAINT profiles_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.schedules
    ADD CONSTRAINT schedules_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.services_offered
    ADD CONSTRAINT services_offered_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.profiles
    ADD CONSTRAINT uk_4ixsj6aqve5pxrbw2u0oyk8bb UNIQUE (user_id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT uk_7tdcd6ab5wsgoudnvj7xf1b7l UNIQUE (user_id);

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT uk_ghpmfn23vmxfu3spu3lfg4r2d UNIQUE (token);

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT uk_ofx66keruapi6vyqpv6f2or37 UNIQUE (name);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT fk1lih5y2npsf8u5o3vhdb9y0os FOREIGN KEY (user_id) REFERENCES public.users(id);

ALTER TABLE ONLY public.profiles
    ADD CONSTRAINT fk410q61iev7klncmpqfuo85ivh FOREIGN KEY (user_id) REFERENCES public.users(id);

ALTER TABLE ONLY public.availabilities
    ADD CONSTRAINT fk85djvpayg0km63rt2wcgfkq3h FOREIGN KEY (provider_id) REFERENCES public.users(id);

ALTER TABLE ONLY public.schedules
    ADD CONSTRAINT fk9wdonp85ncvq3n4drxmmnuquu FOREIGN KEY (client_id) REFERENCES public.users(id);

ALTER TABLE ONLY public.schedules
    ADD CONSTRAINT fkbrtl4mdnmp8h4wrd5iy33x5q4 FOREIGN KEY (service_id) REFERENCES public.services_offered(id);

ALTER TABLE ONLY public.schedules
    ADD CONSTRAINT fkic8edj8fyoprl96n8tf2gpeh6 FOREIGN KEY (provider_id) REFERENCES public.users(id);

ALTER TABLE ONLY public.availability_blocks
    ADD CONSTRAINT fkiy0q3smv1hjw9epsnibdwknid FOREIGN KEY (provider_id) REFERENCES public.users(id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id);

ALTER TABLE ONLY public.services_offered
    ADD CONSTRAINT fkr8ro9o6dmyu16l9qvila5pbcc FOREIGN KEY (provider_id) REFERENCES public.users(id);
