# --- !Ups

ALTER TABLE latin_text ADD COLUMN public BOOLEAN DEFAULT FALSE;
 
# --- !Downs
 
ALTER TABLE latin_text DROP COLUMN IF EXISTS public; 
