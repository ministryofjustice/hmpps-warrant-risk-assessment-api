ALTER TABLE public.warrant_risk_assessment
  ADD COLUMN terminated boolean NULL,
  ADD COLUMN terminated_unterminated_date timestamp with time zone NULL;

ALTER TABLE public.warrant_risk_assessment
  ALTER COLUMN review_required_date TYPE timestamp with time zone
  USING review_required_date AT TIME ZONE 'Europe/London';

