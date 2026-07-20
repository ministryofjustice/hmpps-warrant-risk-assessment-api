ALTER TABLE public.address ADD COLUMN screen varchar(100) NOT NULL DEFAULT '';
ALTER TABLE public.address ALTER COLUMN screen DROP DEFAULT;
ALTER TABLE public.address ADD COLUMN warrant_risk_assessment_id uuid REFERENCES public.warrant_risk_assessment(id);
ALTER TABLE public.address ADD CONSTRAINT xfk1_address_wra
    FOREIGN KEY (warrant_risk_assessment_id) REFERENCES public.warrant_risk_assessment (id) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE public.contact DROP COLUMN contact_date;
ALTER TABLE public.contact DROP COLUMN contact_type_description;
ALTER TABLE public.contact DROP COLUMN contact_outcome;
ALTER TABLE public.contact DROP COLUMN delius_contact_id;
ALTER TABLE public.contact DROP COLUMN form_sent;
ALTER TABLE public.contact ADD COLUMN telephone_number varchar(35) NULL;
ALTER TABLE public.contact ADD COLUMN mobile_number varchar(35) NULL;

