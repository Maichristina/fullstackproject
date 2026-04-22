-- ============================================
-- Job Search Application — Database Schema
-- ============================================

-- 1. Users table
CREATE TABLE IF NOT EXISTS users (
                                     id       BIGSERIAL    PRIMARY KEY,
                                     username VARCHAR(50)  NOT NULL UNIQUE,
    email    VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(20)  NOT NULL
    );

-- 2. Jobs table
CREATE TABLE IF NOT EXISTS jobs (
                                    id           BIGSERIAL    PRIMARY KEY,
                                    title        VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    location     VARCHAR(255),
    salary       DOUBLE PRECISION,
    posted_date  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    posted_by_id BIGINT       NOT NULL,
    CONSTRAINT fk_posted_by
    FOREIGN KEY (posted_by_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );

-- 3. Applications table
CREATE TABLE IF NOT EXISTS applications (
                                            id           BIGSERIAL   PRIMARY KEY,
                                            job_id       BIGINT      NOT NULL,
                                            user_id      BIGINT      NOT NULL,
                                            status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    applied_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_job
    FOREIGN KEY (job_id)
    REFERENCES jobs(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );

-- 4. Insert a default ADMIN user
-- password is: admin123 (BCrypt encoded)
INSERT INTO users (username, email, password, role)
VALUES (
           'admin',
           'admin@jobsearch.com',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           'ROLE_ADMIN'
       ) ON CONFLICT (username) DO NOTHING;