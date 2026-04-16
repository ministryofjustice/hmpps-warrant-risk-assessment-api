CREATE TABLE public.warrant_risk_assessment(id uuid not null primary key,
                                 crn char(7) not null,
                                 national_insurance_number varchar(9) NULL,
                                 title_and_full_name varchar(200) NULL,
                                 date_of_letter date NULL,
                                 sheet_sent_by varchar(200),
                                 telephone_number varchar(35) NULL,
                                 signature varchar(200) NULL,
                                 completed_date timestamp with time zone NULL,
                                 postal_address_id uuid NULL,
                                 date_of_birth date NULL,
                                 last_home_visit_date date NULL,
                                 prison_number varchar(50),
                                 probation_area varchar(100),
                                 risk_to_public_level text,
                                 risk_to_enforcement_officers text,
                                 risk_to_police text,
                                 warrant_executed_by text,
                                 work_address_id uuid NULL,
                                 sign_on_office uuid NULL,
                                 basic_details_saved boolean NULL,
                                 subject_of_mappa_procedures boolean NULL,
                                 high_risk_of_self_harm boolean NULL,
                                 high_risk_of_absconding boolean NULL,
                                 vulnerable boolean NULL,
                                 carry_or_use_weapons boolean NULL,
                                 assaulting_police boolean NULL,
                                 misuse_drugs_and_alcohol boolean NULL,
                                 sign_and_send_saved boolean NULL,
                                 contact_saved boolean NULL,
                                 review_required_date timestamp without time zone NULL,
                                 review_event varchar(100) NULL,
                                 last_updated_datetime timestamp without time zone not NULL,
                                 last_updated_user varchar(100) not NULL,
                                 created_by_user varchar(100) not NULL,
                                 created_datetime timestamp without time zone not NULL);

CREATE TABLE public.address(id uuid not null primary Key,
                            address_id bigint not null,
                            status varchar(100) NULL,
                            office_description varchar(50) NULL,
                            building_name varchar(35) NULL,
                            address_number varchar(35) NULL,
                            street_name varchar(35) NULL,
                            district varchar(35) NULL,
                            town_city varchar(35) NULL,
                            county varchar(35) NULL,
                            postcode varchar(8) NULL,
                            created_by_user varchar(100) not null,
                            created_datetime timestamp without time zone not null,
                            last_updated_user varchar(100) not null,
                            last_updated_datetime timestamp without time zone not null);

CREATE TABLE public.contact(id uuid not null primary key,
                            warrant_risk_assessment_id uuid not null,
                            contact_type_description varchar(200) NULL,
                            contact_person varchar(200),
                            contact_date timestamp without time zone NULL,
                            contact_location_id uuid NULL,
                            created_by_user varchar(100) not null,
                            created_datetime timestamp without time zone NULL,
                            last_updated_user varchar(100) not null,
                            last_updated_datetime timestamp without time zone NULL);

ALTER TABLE public.warrant_risk_assessment ADD CONSTRAINT xfk1_warrant_risk_assessment_postal_address
    FOREIGN KEY (postal_address_id) REFERENCES public.address(id) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE public.warrant_risk_assessment ADD CONSTRAINT xfk2_warrant_risk_assessment_work_address
    FOREIGN KEY (work_address_id) REFERENCES public.address(id) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE public.contact ADD CONSTRAINT xfk3_contact_warrant_risk_assessment
    FOREIGN KEY (warrant_risk_assessment_id) REFERENCES public.warrant_risk_assessment(id) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE public.contact ADD CONSTRAINT xfk4_contact_location_address
    FOREIGN KEY (contact_location_id) REFERENCES public.address (id);


CREATE TABLE public.screen_information (
   id uuid NOT NULL PRIMARY KEY,
   screen_name varchar(200) NULL,
   field_name varchar(200) NULL,
   field_text varchar(20000) NULL
);
