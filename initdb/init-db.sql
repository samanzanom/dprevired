CREATE TABLE IF NOT EXISTS public.users
(
    id       BIGSERIAL PRIMARY KEY,
    password VARCHAR(100),
    username VARCHAR(500)
    );

ALTER TABLE public.users OWNER TO previred;

CREATE TABLE IF NOT EXISTS public.companys
(
    id                 VARCHAR(50) NOT NULL PRIMARY KEY,
    company_name       VARCHAR(200),
    rut                VARCHAR(12),
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6)
    );

ALTER TABLE public.companys OWNER TO previred;

CREATE TABLE IF NOT EXISTS public.workers
(
    id                 BIGSERIAL PRIMARY KEY,
    first_surname      VARCHAR(100),
    names              VARCHAR(200),
    rut                VARCHAR(12),
    second_surname     VARCHAR(100),
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    company_id         VARCHAR(50) NOT NULL,
    CONSTRAINT fk_workers_companys FOREIGN KEY (company_id) REFERENCES public.companys(id)
    );

ALTER TABLE public.workers OWNER TO previred;
