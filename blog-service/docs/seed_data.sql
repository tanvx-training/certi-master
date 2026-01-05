-- =====================================================
-- Blog Service Seed Data
-- Sample categories and tags for CertiMaster Blog Service
-- =====================================================

-- =====================================================
-- CATEGORIES
-- Main certification and technology categories
-- =====================================================
INSERT INTO post_categories (name, slug, description, created_at, created_by) VALUES
-- Cloud Certifications
('AWS', 'aws', 'Amazon Web Services certifications and cloud computing topics', CURRENT_TIMESTAMP, 'system'),
('Azure', 'azure', 'Microsoft Azure certifications and cloud services', CURRENT_TIMESTAMP, 'system'),
('Google Cloud', 'google-cloud', 'Google Cloud Platform certifications and services', CURRENT_TIMESTAMP, 'system'),

-- DevOps & Infrastructure
('DevOps', 'devops', 'DevOps practices, CI/CD, and automation topics', CURRENT_TIMESTAMP, 'system'),
('Kubernetes', 'kubernetes', 'Container orchestration and Kubernetes certifications', CURRENT_TIMESTAMP, 'system'),
('Docker', 'docker', 'Containerization and Docker-related content', CURRENT_TIMESTAMP, 'system'),

-- Security
('Security', 'security', 'Cybersecurity certifications and best practices', CURRENT_TIMESTAMP, 'system'),
('CompTIA', 'comptia', 'CompTIA certifications (Security+, Network+, A+)', CURRENT_TIMESTAMP, 'system'),

-- Development
('Java', 'java', 'Java programming and Oracle certifications', CURRENT_TIMESTAMP, 'system'),
('Python', 'python', 'Python programming and related certifications', CURRENT_TIMESTAMP, 'system'),

-- Networking
('Networking', 'networking', 'Network engineering and Cisco certifications', CURRENT_TIMESTAMP, 'system'),
('Cisco', 'cisco', 'Cisco certifications (CCNA, CCNP, CCIE)', CURRENT_TIMESTAMP, 'system'),

-- Project Management
('Project Management', 'project-management', 'PMP, Agile, and project management certifications', CURRENT_TIMESTAMP, 'system'),
('Agile', 'agile', 'Agile methodologies and Scrum certifications', CURRENT_TIMESTAMP, 'system'),

-- Data & AI
('Data Science', 'data-science', 'Data science and analytics certifications', CURRENT_TIMESTAMP, 'system'),
('Machine Learning', 'machine-learning', 'AI and machine learning certifications', CURRENT_TIMESTAMP, 'system');

-- =====================================================
-- TAGS
-- Specific topics and keywords for blog posts
-- =====================================================
INSERT INTO post_tags (name, slug, created_at, created_by) VALUES
-- AWS Specific Tags
('AWS Solutions Architect', 'aws-solutions-architect', CURRENT_TIMESTAMP, 'system'),
('AWS Developer', 'aws-developer', CURRENT_TIMESTAMP, 'system'),
('AWS SysOps', 'aws-sysops', CURRENT_TIMESTAMP, 'system'),
('AWS DevOps', 'aws-devops', CURRENT_TIMESTAMP, 'system'),
('EC2', 'ec2', CURRENT_TIMESTAMP, 'system'),
('S3', 's3', CURRENT_TIMESTAMP, 'system'),
('Lambda', 'lambda', CURRENT_TIMESTAMP, 'system'),
('RDS', 'rds', CURRENT_TIMESTAMP, 'system'),
('VPC', 'vpc', CURRENT_TIMESTAMP, 'system'),
('IAM', 'iam', CURRENT_TIMESTAMP, 'system'),

-- Azure Specific Tags
('Azure Administrator', 'azure-administrator', CURRENT_TIMESTAMP, 'system'),
('Azure Developer', 'azure-developer', CURRENT_TIMESTAMP, 'system'),
('Azure Solutions Architect', 'azure-solutions-architect', CURRENT_TIMESTAMP, 'system'),
('Azure DevOps', 'azure-devops', CURRENT_TIMESTAMP, 'system'),

-- DevOps Tags
('CI/CD', 'ci-cd', CURRENT_TIMESTAMP, 'system'),
('Jenkins', 'jenkins', CURRENT_TIMESTAMP, 'system'),
('GitHub Actions', 'github-actions', CURRENT_TIMESTAMP, 'system'),
('Terraform', 'terraform', CURRENT_TIMESTAMP, 'system'),
('Ansible', 'ansible', CURRENT_TIMESTAMP, 'system'),
('Infrastructure as Code', 'infrastructure-as-code', CURRENT_TIMESTAMP, 'system'),

-- Container Tags
('Docker Compose', 'docker-compose', CURRENT_TIMESTAMP, 'system'),
('Kubernetes CKA', 'kubernetes-cka', CURRENT_TIMESTAMP, 'system'),
('Kubernetes CKAD', 'kubernetes-ckad', CURRENT_TIMESTAMP, 'system'),
('Helm', 'helm', CURRENT_TIMESTAMP, 'system'),

-- Security Tags
('Security+', 'security-plus', CURRENT_TIMESTAMP, 'system'),
('CISSP', 'cissp', CURRENT_TIMESTAMP, 'system'),
('CEH', 'ceh', CURRENT_TIMESTAMP, 'system'),
('Penetration Testing', 'penetration-testing', CURRENT_TIMESTAMP, 'system'),
('Network Security', 'network-security', CURRENT_TIMESTAMP, 'system'),

-- Programming Tags
('Spring Boot', 'spring-boot', CURRENT_TIMESTAMP, 'system'),
('Microservices', 'microservices', CURRENT_TIMESTAMP, 'system'),
('REST API', 'rest-api', CURRENT_TIMESTAMP, 'system'),
('JPA', 'jpa', CURRENT_TIMESTAMP, 'system'),
('Django', 'django', CURRENT_TIMESTAMP, 'system'),
('FastAPI', 'fastapi', CURRENT_TIMESTAMP, 'system'),

-- Networking Tags
('CCNA', 'ccna', CURRENT_TIMESTAMP, 'system'),
('CCNP', 'ccnp', CURRENT_TIMESTAMP, 'system'),
('Network+', 'network-plus', CURRENT_TIMESTAMP, 'system'),
('TCP/IP', 'tcp-ip', CURRENT_TIMESTAMP, 'system'),
('Routing', 'routing', CURRENT_TIMESTAMP, 'system'),
('Switching', 'switching', CURRENT_TIMESTAMP, 'system'),

-- General Tags
('Certification Tips', 'certification-tips', CURRENT_TIMESTAMP, 'system'),
('Study Guide', 'study-guide', CURRENT_TIMESTAMP, 'system'),
('Practice Exam', 'practice-exam', CURRENT_TIMESTAMP, 'system'),
('Career Advice', 'career-advice', CURRENT_TIMESTAMP, 'system'),
('Interview Prep', 'interview-prep', CURRENT_TIMESTAMP, 'system'),
('Best Practices', 'best-practices', CURRENT_TIMESTAMP, 'system'),
('Tutorial', 'tutorial', CURRENT_TIMESTAMP, 'system'),
('Hands-on Lab', 'hands-on-lab', CURRENT_TIMESTAMP, 'system'),
('Beginner', 'beginner', CURRENT_TIMESTAMP, 'system'),
('Advanced', 'advanced', CURRENT_TIMESTAMP, 'system');
