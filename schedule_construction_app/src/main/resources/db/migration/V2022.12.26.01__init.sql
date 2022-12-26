-- Database generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler version: 0.9.4
-- PostgreSQL version: 14.0
-- Project Site: pgmodeler.io

CREATE TABLE teacher (
	id uuid NOT NULL,
	name varchar(512) NOT NULL,
	surname varchar(512) NOT NULL,
	lastname varchar(512) NOT NULL,
	qualification_id uuid NOT NULL,
	CONSTRAINT teacher_pk PRIMARY KEY (id)
);

CREATE TABLE training_class (
	id uuid NOT NULL,
	teacher_id uuid NOT NULL,
	curriculum_id uuid NOT NULL,
	training_class_grade_id uuid NOT NULL,
	training_shift_id uuid NOT NULL,
	CONSTRAINT training_class_pk PRIMARY KEY (id)
);

ALTER TABLE training_class ADD CONSTRAINT teacher_fk FOREIGN KEY (teacher_id)
REFERENCES teacher (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE curriculum (
	id uuid NOT NULL,
	code varchar(512) NOT NULL,
	training_class_grade_id uuid NOT NULL,
	CONSTRAINT curriculum_pk PRIMARY KEY (id)
);

CREATE TABLE discipline (
	id uuid NOT NULL,
	code varchar(512) NOT NULL,
	CONSTRAINT discipline_pk PRIMARY KEY (id)
);

CREATE TABLE work_program (
	id uuid NOT NULL,
	academic_hours smallint NOT NULL,
	discipline_id uuid NOT NULL,
	curriculum_id uuid NOT NULL,
	training_class_grade_id uuid NOT NULL,
	teacher_id uuid NOT NULL,
	distribution_type_id smallint NOT NULL,
	CONSTRAINT curriculum_part_pk PRIMARY KEY (id)
);

ALTER TABLE work_program ADD CONSTRAINT discipline_fk FOREIGN KEY (discipline_id)
REFERENCES discipline (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE work_program ADD CONSTRAINT curriculum_fk FOREIGN KEY (curriculum_id)
REFERENCES curriculum (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE training_class ADD CONSTRAINT curriculum_fk FOREIGN KEY (curriculum_id)
REFERENCES curriculum (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE training_class_grade (
	id uuid NOT NULL,
	value smallint NOT NULL,
	CONSTRAINT training_class_grade_pk PRIMARY KEY (id)
);

ALTER TABLE training_class ADD CONSTRAINT training_class_grade_fk FOREIGN KEY (training_class_grade_id)
REFERENCES training_class_grade (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE work_program ADD CONSTRAINT training_class_grade_fk FOREIGN KEY (training_class_grade_id)
REFERENCES training_class_grade (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE curriculum ADD CONSTRAINT training_class_grade_fk FOREIGN KEY (training_class_grade_id)
REFERENCES training_class_grade (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE qualification (
	id uuid NOT NULL,
	code varchar(512) NOT NULL,
	CONSTRAINT qualification_pk PRIMARY KEY (id)
);

ALTER TABLE teacher ADD CONSTRAINT qualification_fk FOREIGN KEY (qualification_id)
REFERENCES qualification (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE qualification_discipline (
	id uuid NOT NULL,
	discipline_id uuid NOT NULL,
	qualification_id uuid NOT NULL,
	CONSTRAINT qualification_discipline_pk PRIMARY KEY (id)
);

ALTER TABLE qualification_discipline ADD CONSTRAINT discipline_fk FOREIGN KEY (discipline_id)
REFERENCES discipline (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE qualification_discipline ADD CONSTRAINT qualification_fk FOREIGN KEY (qualification_id)
REFERENCES qualification (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE lesson (
	id uuid NOT NULL,
	classroom_id uuid NOT NULL,
	discipline_id uuid NOT NULL,
	training_class_subgroup_id uuid NOT NULL,
	CONSTRAINT lesson_pk PRIMARY KEY (id)
);

CREATE TABLE classroom (
	id uuid NOT NULL,
	code varchar(512) NOT NULL,
	classroom_type_id uuid NOT NULL,
	CONSTRAINT classroom_pk PRIMARY KEY (id)
);

ALTER TABLE lesson ADD CONSTRAINT classroom_fk FOREIGN KEY (classroom_id)
REFERENCES classroom (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE classroom_type (
	id uuid NOT NULL,
	code varchar(512) NOT NULL,
	CONSTRAINT classroom_type_pk PRIMARY KEY (id)
);

ALTER TABLE classroom ADD CONSTRAINT classroom_type_fk FOREIGN KEY (classroom_type_id)
REFERENCES classroom_type (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE classroom_type_discipline (
	id uuid NOT NULL,
	discipline_id uuid NOT NULL,
	classroom_type_id uuid NOT NULL,
	CONSTRAINT classroom_type_discipline_pk PRIMARY KEY (id)
);

ALTER TABLE classroom_type_discipline ADD CONSTRAINT discipline_fk FOREIGN KEY (discipline_id)
REFERENCES discipline (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE classroom_type_discipline ADD CONSTRAINT classroom_type_fk FOREIGN KEY (classroom_type_id)
REFERENCES classroom_type (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE schedule_template (
	id uuid NOT NULL,
	CONSTRAINT schedule_pk PRIMARY KEY (id)
);

ALTER TABLE work_program ADD CONSTRAINT teacher_fk FOREIGN KEY (teacher_id)
REFERENCES teacher (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE lesson ADD CONSTRAINT discipline_fk FOREIGN KEY (discipline_id)
REFERENCES discipline (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE training_shift (
	id uuid NOT NULL,
	code smallint NOT NULL,
	CONSTRAINT training_shift_pk PRIMARY KEY (id)
);

ALTER TABLE training_class ADD CONSTRAINT training_shift_fk FOREIGN KEY (training_shift_id)
REFERENCES training_shift (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE template_slot (
	id uuid NOT NULL,
	order_number smallint NOT NULL,
	start_time time NOT NULL,
	end_time time NOT NULL,
	schedule_template_id uuid NOT NULL,
	CONSTRAINT template_slot_pk PRIMARY KEY (id)
);

ALTER TABLE template_slot ADD CONSTRAINT schedule_template_fk FOREIGN KEY (schedule_template_id)
REFERENCES schedule_template (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE distribution_type (
	id smallint NOT NULL,
	code varchar(512) NOT NULL,
	CONSTRAINT distribution_type_pk PRIMARY KEY (id)
);

ALTER TABLE work_program ADD CONSTRAINT distribution_type_fk FOREIGN KEY (distribution_type_id)
REFERENCES distribution_type (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE training_class_subgroup (
	id uuid NOT NULL,
	code varchar(512) NOT NULL,
	training_class_id uuid NOT NULL,
	CONSTRAINT training_class_subgroup_pk PRIMARY KEY (id)
);

ALTER TABLE training_class_subgroup ADD CONSTRAINT training_class_fk FOREIGN KEY (training_class_id)
REFERENCES training_class (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE lesson ADD CONSTRAINT training_class_subgroup_fk FOREIGN KEY (training_class_subgroup_id)
REFERENCES training_class_subgroup (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;


