ALTER TABLE public.profiles ADD COLUMN code character varying(6);
UPDATE public.profiles SET code = upper(substr(replace(id::text, '-', ''), 1, 6)) WHERE code IS NULL;
ALTER TABLE public.profiles ALTER COLUMN code SET NOT NULL;
ALTER TABLE public.profiles ADD CONSTRAINT profiles_code_unique UNIQUE (code);
