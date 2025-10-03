CREATE DATABASE IF NOT EXISTS microfinance DEFAULT CHARACTER SET utf8mb4;
USE microfinance;

CREATE TABLE personne (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  date_naissance DATE NOT NULL,
  ville VARCHAR(100),
  nombre_enfants INT NOT NULL DEFAULT 0,
  investissement BOOLEAN NOT NULL DEFAULT FALSE,
  placement BOOLEAN NOT NULL DEFAULT FALSE,
  situation_familiale VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  score INT NULL,
  type VARCHAR(20) NOT NULL CHECK (type IN ('EMPLOYE','PROFESSIONNEL'))
);

CREATE TABLE employe (
  personne_id BIGINT PRIMARY KEY,
  salaire DECIMAL(12,2) NOT NULL,
  anciennete_years INT NOT NULL,
  poste VARCHAR(100),
  type_contrat VARCHAR(40),
  secteur VARCHAR(40),
  CONSTRAINT fk_employe_personne FOREIGN KEY (personne_id) REFERENCES personne(id) ON DELETE CASCADE
);

CREATE TABLE professionnel (
  personne_id BIGINT PRIMARY KEY,
  revenu DECIMAL(12,2) NOT NULL,
  immatriculation_fiscale VARCHAR(50),
  secteur_activite VARCHAR(100),
  activite VARCHAR(100),
  CONSTRAINT fk_professionnel_personne FOREIGN KEY (personne_id) REFERENCES personne(id) ON DELETE CASCADE
);

CREATE TABLE credit (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  personne_id BIGINT NOT NULL,
  date_credit DATE NOT NULL,
  montant_demande DECIMAL(12,2) NOT NULL,
  montant_octroye DECIMAL(12,2) NULL,
  taux_interet DECIMAL(8,5) NOT NULL,
  duree_mois INT NOT NULL,
  type_credit VARCHAR(50),
  decision VARCHAR(40),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_credit_personne (personne_id),
  CONSTRAINT fk_credit_personne FOREIGN KEY (personne_id) REFERENCES personne(id) ON DELETE CASCADE
);

CREATE TABLE echeance (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  credit_id BIGINT NOT NULL,
  date_echeance DATE NOT NULL,
  mensualite DECIMAL(12,2) NOT NULL,
  date_paiement DATE NULL,
  statut_paiement VARCHAR(20) NOT NULL,
  INDEX idx_echeance_credit (credit_id),
  CONSTRAINT fk_echeance_credit FOREIGN KEY (credit_id) REFERENCES credit(id) ON DELETE CASCADE
);

CREATE TABLE incident (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  date_incident DATE NOT NULL,
  echeance_id BIGINT NOT NULL,
  type_incident VARCHAR(20) NOT NULL,
  impact_score INT NOT NULL,
  note VARCHAR(255),
  INDEX idx_incident_echeance (echeance_id),
  CONSTRAINT fk_incident_echeance FOREIGN KEY (echeance_id) REFERENCES echeance(id) ON DELETE CASCADE
);

CREATE TABLE audit (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  entity_type VARCHAR(60) NOT NULL,
  entity_id BIGINT NOT NULL,
  field VARCHAR(100) NOT NULL,
  old_value VARCHAR(255),
  new_value VARCHAR(255),
  changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reason VARCHAR(255),
  INDEX idx_audit_entity (entity_type, entity_id)
);

