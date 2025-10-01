-- create DB
CREATE DATABASE IF NOT EXISTS credit_scoring;
USE credit_scoring;

-- Table personne (parent)
CREATE TABLE personne (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE NOT NULL,
    ville VARCHAR(100),
    nombre_enfants INT DEFAULT 0,
    investissement DECIMAL(15,2) DEFAULT 0,
    placement DECIMAL(15,2) DEFAULT 0,
    situation_familiale ENUM('MARIE','CELIBATAIRE','DIVORCE','VEUF'),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    score INT DEFAULT 0,
    type_personne ENUM('EMPLOYE','PROFESSIONNEL') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table employe (héritage one-to-one)
CREATE TABLE employe (
    id INT UNSIGNED PRIMARY KEY,
    salaire DECIMAL(15,2) NOT NULL,
    anciennete INT DEFAULT 0,
    poste VARCHAR(100),
    type_contrat ENUM('CDI','CDD','INTERIM','AUTO_ENTREPRENEUR','LIBERAL'),
    secteur ENUM('PUBLIC','GRANDE_ENTREPRISE','PME'),
    FOREIGN KEY (id) REFERENCES personne(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table professionnel (héritage one-to-one)
CREATE TABLE professionnel (
    id INT UNSIGNED PRIMARY KEY,
    revenu DECIMAL(15,2) NOT NULL,
    immatriculation_fiscale VARCHAR(50),
    secteur_activite ENUM('AGRICULTURE','SERVICE','COMMERCE','CONSTRUCTION','INDUSTRIE'),
    activite VARCHAR(100),
    FOREIGN KEY (id) REFERENCES personne(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table credit
CREATE TABLE credit (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_client INT UNSIGNED NOT NULL,
    date_credit DATE NOT NULL,
    montant_demande DECIMAL(15,2) NOT NULL,
    montant_octroye DECIMAL(15,2),
    taux_interet DECIMAL(5,2),
    duree_mois INT,
    type_credit ENUM('CONSOMMATION','IMMOBILIER','AUTO','ETUDES'),
    decision ENUM('ACCORD_IMMEDIAT','ETUDE_MANUELLE','REFUS_AUTOMATIQUE'),
    FOREIGN KEY (id_client) REFERENCES personne(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table echeance
CREATE TABLE echeance (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_credit INT UNSIGNED NOT NULL,
    date_echeance DATE NOT NULL,
    mensualite DECIMAL(15,2) NOT NULL,
    date_paiement DATE,
    statut_paiement ENUM('PAYE_A_TEMPS','EN_RETARD','PAYE_EN_RETARD','IMPAYE_NON_REGLE','IMPAYE_REGLE'),
    FOREIGN KEY (id_credit) REFERENCES credit(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table incident
CREATE TABLE incident (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_echeance INT UNSIGNED,
    date_incident DATE NOT NULL,
    type_incident ENUM('PAYE_A_TEMPS','EN_RETARD','PAYE_EN_RETARD','IMPAYE_NON_REGLE','IMPAYE_REGLE'),
    score_impact INT,
    description VARCHAR(255),
    FOREIGN KEY (id_echeance) REFERENCES echeance(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
