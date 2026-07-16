ALTER TABLE public.address
    ALTER COLUMN building_name TYPE varchar(80),
    ALTER COLUMN street_name TYPE varchar(80),
    ALTER COLUMN district TYPE varchar(80),
    ALTER COLUMN delius_address_id DROP NOT NULL;

