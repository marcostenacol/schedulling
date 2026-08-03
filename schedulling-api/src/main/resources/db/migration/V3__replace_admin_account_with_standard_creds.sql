DELETE FROM public.profiles
WHERE user_id IN (SELECT id FROM public.users WHERE email = 'marcos.tenacol@ae3tecnologia.com.br');

DELETE FROM public.refresh_tokens
WHERE user_id IN (SELECT id FROM public.users WHERE email = 'marcos.tenacol@ae3tecnologia.com.br');

DELETE FROM public.users
WHERE email = 'marcos.tenacol@ae3tecnologia.com.br';

INSERT INTO public.users (id, email, password, role_id, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'admin@agenda.mvndev.online',
    '$2b$10$4B0fRaIciG8GDPFOMGV9fOmC5/Spqxxf1XVxYHC3IMwCmnr86FKk2',
    (SELECT id FROM public.roles WHERE name = 'ROLE_ADMIN'),
    now(),
    now()
WHERE NOT EXISTS (SELECT 1 FROM public.users WHERE email = 'admin@agenda.mvndev.online');

INSERT INTO public.profiles (id, user_id, name, type, created_at, updated_at)
SELECT
    gen_random_uuid(),
    u.id,
    'Admin',
    'admin',
    now(),
    now()
FROM public.users u
WHERE u.email = 'admin@agenda.mvndev.online'
  AND NOT EXISTS (SELECT 1 FROM public.profiles p WHERE p.user_id = u.id);
