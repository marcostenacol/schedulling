ALTER TABLE public.roles DROP CONSTRAINT roles_name_check;

ALTER TABLE public.roles
    ADD CONSTRAINT roles_name_check
    CHECK (((name)::text = ANY ((ARRAY['ROLE_CLIENT'::character varying, 'ROLE_PROVIDER'::character varying, 'ROLE_ADMIN'::character varying])::text[])));

INSERT INTO public.roles (id, name, created_at, updated_at)
SELECT gen_random_uuid(), 'ROLE_ADMIN', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM public.roles WHERE name = 'ROLE_ADMIN');

INSERT INTO public.users (id, email, password, role_id, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'marcos.tenacol@ae3tecnologia.com.br',
    '$2b$10$CLgR3br51impbSY3JE6Anu1jqzNoxhj2/VLfgRlxiildvRjuoYcx.',
    (SELECT id FROM public.roles WHERE name = 'ROLE_ADMIN'),
    now(),
    now()
WHERE NOT EXISTS (SELECT 1 FROM public.users WHERE email = 'marcos.tenacol@ae3tecnologia.com.br');

INSERT INTO public.profiles (id, user_id, name, type, created_at, updated_at)
SELECT
    gen_random_uuid(),
    u.id,
    'Marcos',
    'admin',
    now(),
    now()
FROM public.users u
WHERE u.email = 'marcos.tenacol@ae3tecnologia.com.br'
  AND NOT EXISTS (SELECT 1 FROM public.profiles p WHERE p.user_id = u.id);
