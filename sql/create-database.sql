-- Выполняется один раз администратором PostgreSQL через psql.
-- Пароль передаётся через параметр psql -v lab_password.
\set ON_ERROR_STOP on
SELECT format('CREATE ROLE soa_lab1 LOGIN PASSWORD %L', :'lab_password')
WHERE NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'soa_lab1')
\gexec
SELECT 'CREATE DATABASE soa_lab1 OWNER soa_lab1 ENCODING ''UTF8'' TEMPLATE template0'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'soa_lab1')
\gexec
