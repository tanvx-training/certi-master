-- =====================================================
-- Blog Service - Posts Seed Data
-- Sample blog posts for CertiMaster platform
-- =====================================================

-- =====================================================
-- POSTS DATA
-- Includes DRAFT, PUBLISHED, and ARCHIVED posts
-- =====================================================

-- Post 1: PUBLISHED - AWS Solutions Architect Guide
INSERT INTO posts (
    title, slug, content, content_html, excerpt, featured_image,
    author_id, status, published_at, views_count, likes_count, comments_count,
    reading_time_minutes, seo_title, seo_description, seo_keywords,
    created_at, updated_at, created_by, updated_by
) VALUES (
    'Complete Guide to AWS Solutions Architect Associate Certification',
    'complete-guide-aws-solutions-architect-associate-certification',
    '# Complete Guide to AWS Solutions Architect Associate Certification

## Introduction

The AWS Solutions Architect Associate certification is one of the most sought-after cloud certifications in the industry. This comprehensive guide will help you prepare effectively for the exam.

## Exam Overview

- **Exam Code**: SAA-C03
- **Duration**: 130 minutes
- **Questions**: 65 questions
- **Passing Score**: 720/1000
- **Cost**: $150 USD

## Key Domains

### 1. Design Secure Architectures (30%)
- IAM policies and roles
- VPC security groups and NACLs
- Encryption at rest and in transit

### 2. Design Resilient Architectures (26%)
- Multi-AZ deployments
- Auto Scaling groups
- Disaster recovery strategies

### 3. Design High-Performing Architectures (24%)
- EC2 instance types selection
- EBS volume optimization
- CloudFront distributions

### 4. Design Cost-Optimized Architectures (20%)
- Reserved Instances vs On-Demand
- S3 storage classes
- Cost allocation tags

## Study Resources

1. AWS Official Documentation
2. AWS Skill Builder
3. Practice exams on CertiMaster
4. Hands-on labs

## Tips for Success

- Focus on hands-on experience
- Take multiple practice exams
- Understand the Well-Architected Framework
- Review AWS whitepapers

Good luck with your certification journey!',
    '<h1>Complete Guide to AWS Solutions Architect Associate Certification</h1><h2>Introduction</h2><p>The AWS Solutions Architect Associate certification is one of the most sought-after cloud certifications in the industry...</p>',
    'A comprehensive guide to help you prepare for the AWS Solutions Architect Associate certification exam, covering all domains and study strategies.',
    'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800',
    1, 'PUBLISHED', '2025-12-15 10:00:00', 1250, 89, 23,
    8, 'AWS Solutions Architect Associate Certification Guide 2025',
    'Complete preparation guide for AWS SAA-C03 exam with study tips, resources, and domain breakdown.',
    'AWS, Solutions Architect, SAA-C03, Cloud Certification, AWS Exam',
    '2025-12-10 09:00:00', '2025-12-15 10:00:00', 'admin', 'admin'
);

-- Post 2: PUBLISHED - Kubernetes CKA Preparation
INSERT INTO posts (
    title, slug, content, content_html, excerpt, featured_image,
    author_id, status, published_at, views_count, likes_count, comments_count,
    reading_time_minutes, seo_title, seo_description, seo_keywords,
    created_at, updated_at, created_by, updated_by
) VALUES (
    'How to Pass Kubernetes CKA Exam on Your First Attempt',
    'how-to-pass-kubernetes-cka-exam-first-attempt',
    '# How to Pass Kubernetes CKA Exam on Your First Attempt

## What is CKA?

The Certified Kubernetes Administrator (CKA) exam is a performance-based certification that tests your ability to perform tasks in a live Kubernetes environment.

## Exam Format

- **Duration**: 2 hours
- **Format**: Performance-based (hands-on)
- **Passing Score**: 66%
- **Environment**: Remote proctored

## Core Competencies

### Cluster Architecture (25%)
```bash
# Check cluster info
kubectl cluster-info
kubectl get nodes
```

### Workloads & Scheduling (15%)
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
```

### Services & Networking (20%)
- ClusterIP, NodePort, LoadBalancer
- Network Policies
- Ingress controllers

### Storage (10%)
- Persistent Volumes
- Storage Classes
- Volume Claims

### Troubleshooting (30%)
- Pod debugging
- Node troubleshooting
- Network issues

## Practice Environment

Use these tools for practice:
1. Minikube
2. Kind (Kubernetes in Docker)
3. Killer.sh simulator

## Time Management Tips

- Use kubectl aliases
- Master imperative commands
- Practice with time constraints

You got this!',
    '<h1>How to Pass Kubernetes CKA Exam on Your First Attempt</h1><h2>What is CKA?</h2><p>The Certified Kubernetes Administrator (CKA) exam is a performance-based certification...</p>',
    'Essential tips and strategies to pass the Kubernetes CKA certification exam on your first attempt with hands-on practice recommendations.',
    'https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?w=800',
    2, 'PUBLISHED', '2025-12-20 14:30:00', 890, 67, 15,
    6, 'Pass Kubernetes CKA Exam First Attempt - Complete Guide',
    'Learn how to pass the CKA exam with practical tips, study resources, and time management strategies.',
    'Kubernetes, CKA, Container Orchestration, DevOps, K8s Certification',
    '2025-12-18 11:00:00', '2025-12-20 14:30:00', 'admin', 'admin'
);

-- Post 3: PUBLISHED - DevOps CI/CD Best Practices
INSERT INTO posts (
    title, slug, content, content_html, excerpt, featured_image,
    author_id, status, published_at, views_count, likes_count, comments_count,
    reading_time_minutes, seo_title, seo_description, seo_keywords,
    created_at, updated_at, created_by, updated_by
) VALUES (
    'DevOps CI/CD Pipeline Best Practices for 2025',
    'devops-ci-cd-pipeline-best-practices-2025',
    '# DevOps CI/CD Pipeline Best Practices for 2025

## Introduction

Continuous Integration and Continuous Deployment (CI/CD) are fundamental practices in modern software development. This article covers the best practices for building robust pipelines.

## Key Principles

### 1. Automate Everything
- Build automation
- Test automation
- Deployment automation
- Infrastructure as Code

### 2. Fail Fast
```yaml
# GitHub Actions example
name: CI Pipeline
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Run tests
        run: npm test
```

### 3. Keep Pipelines Fast
- Parallel test execution
- Caching dependencies
- Incremental builds

## Pipeline Stages

1. **Source**: Code commit triggers pipeline
2. **Build**: Compile and package application
3. **Test**: Unit, integration, and e2e tests
4. **Security Scan**: SAST/DAST analysis
5. **Deploy**: Staging then production

## Tools Comparison

| Tool | Best For | Learning Curve |
|------|----------|----------------|
| GitHub Actions | GitHub repos | Low |
| Jenkins | Enterprise | Medium |
| GitLab CI | GitLab users | Low |
| CircleCI | Cloud-native | Low |

## Security Considerations

- Secrets management
- Image scanning
- Dependency auditing
- RBAC for pipelines

## Monitoring & Observability

Track these metrics:
- Deployment frequency
- Lead time for changes
- Change failure rate
- Mean time to recovery

Implement these practices to level up your DevOps game!',
    '<h1>DevOps CI/CD Pipeline Best Practices for 2025</h1><h2>Introduction</h2><p>Continuous Integration and Continuous Deployment (CI/CD) are fundamental practices...</p>',
    'Learn the essential CI/CD pipeline best practices for 2025, including automation strategies, security considerations, and tool comparisons.',
    'https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?w=800',
    1, 'PUBLISHED', '2025-12-22 09:00:00', 2100, 156, 42,
    10, 'CI/CD Pipeline Best Practices 2025 - DevOps Guide',
    'Comprehensive guide to CI/CD best practices including automation, security, and monitoring strategies for modern DevOps teams.',
    'DevOps, CI/CD, GitHub Actions, Jenkins, Automation, Pipeline',
    '2025-12-20 16:00:00', '2025-12-22 09:00:00', 'admin', 'admin'
);

-- Post 4: DRAFT - Azure AZ-104 Study Guide (not published yet)
INSERT INTO posts (
    title, slug, content, content_html, excerpt, featured_image,
    author_id, status, published_at, views_count, likes_count, comments_count,
    reading_time_minutes, seo_title, seo_description, seo_keywords,
    created_at, updated_at, created_by, updated_by
) VALUES (
    'Azure Administrator AZ-104 Study Guide',
    'azure-administrator-az-104-study-guide',
    '# Azure Administrator AZ-104 Study Guide

## Overview

The AZ-104 exam measures your ability to manage Azure identities and governance, implement and manage storage, deploy and manage Azure compute resources, configure and manage virtual networking, and monitor and maintain Azure resources.

## Exam Domains

### 1. Manage Azure Identities and Governance (20-25%)
- Azure AD users and groups
- RBAC roles
- Azure Policy
- Resource locks

### 2. Implement and Manage Storage (15-20%)
- Storage accounts
- Blob storage
- Azure Files
- Storage security

### 3. Deploy and Manage Azure Compute (20-25%)
- Virtual machines
- App Services
- Container instances
- Azure Kubernetes Service

### 4. Configure and Manage Virtual Networking (20-25%)
- Virtual networks
- NSGs and ASGs
- Azure DNS
- VPN Gateway

### 5. Monitor and Maintain Azure Resources (10-15%)
- Azure Monitor
- Log Analytics
- Alerts and metrics
- Backup and recovery

## Study Resources

- Microsoft Learn paths
- Azure documentation
- Hands-on labs
- Practice assessments

*This guide is still being developed...*',
    NULL,
    'Comprehensive study guide for the Microsoft Azure Administrator AZ-104 certification exam.',
    'https://images.unsplash.com/photo-1633419461186-7d40a38105ec?w=800',
    2, 'DRAFT', NULL, 0, 0, 0,
    7, 'Azure AZ-104 Study Guide - Complete Preparation',
    'Master the Azure Administrator certification with this comprehensive AZ-104 study guide.',
    'Azure, AZ-104, Microsoft Certification, Cloud Admin, Azure Administrator',
    '2025-12-28 10:00:00', '2025-12-28 10:00:00', 'admin', 'admin'
);

-- Post 5: DRAFT - Spring Boot Microservices (work in progress)
INSERT INTO posts (
    title, slug, content, content_html, excerpt, featured_image,
    author_id, status, published_at, views_count, likes_count, comments_count,
    reading_time_minutes, seo_title, seo_description, seo_keywords,
    created_at, updated_at, created_by, updated_by
) VALUES (
    'Building Microservices with Spring Boot 3',
    'building-microservices-spring-boot-3',
    '# Building Microservices with Spring Boot 3

## Introduction

Spring Boot 3 brings exciting new features for building cloud-native microservices. This tutorial covers the essential patterns and practices.

## Prerequisites

- Java 17+
- Maven or Gradle
- Docker
- Basic Spring knowledge

## Project Setup

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

## Key Components

### Service Discovery
Using Spring Cloud Netflix Eureka...

### API Gateway
Spring Cloud Gateway configuration...

### Config Server
Centralized configuration management...

*More content coming soon...*',
    NULL,
    'Learn how to build production-ready microservices using Spring Boot 3 with service discovery, API gateway, and more.',
    'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800',
    1, 'DRAFT', NULL, 0, 0, 0,
    12, 'Spring Boot 3 Microservices Tutorial',
    'Complete guide to building microservices architecture with Spring Boot 3, Spring Cloud, and Docker.',
    'Spring Boot, Microservices, Java, Spring Cloud, Docker',
    '2025-12-30 14:00:00', '2025-12-30 14:00:00', 'admin', 'admin'
);

-- Post 6: ARCHIVED - Old AWS SAA-C02 Guide (outdated)
INSERT INTO posts (
    title, slug, content, content_html, excerpt, featured_image,
    author_id, status, published_at, views_count, likes_count, comments_count,
    reading_time_minutes, seo_title, seo_description, seo_keywords,
    created_at, updated_at, created_by, updated_by
) VALUES (
    'AWS Solutions Architect SAA-C02 Exam Guide (Retired)',
    'aws-solutions-architect-saa-c02-exam-guide-retired',
    '# AWS Solutions Architect SAA-C02 Exam Guide

> **Note**: This exam has been retired. Please see our updated SAA-C03 guide.

## Exam Overview

The SAA-C02 exam was the previous version of the AWS Solutions Architect Associate certification.

## Key Topics Covered

- EC2 and compute services
- S3 and storage solutions
- VPC networking
- Database services
- Security best practices

## Why This Exam Was Retired

AWS regularly updates their certifications to reflect current services and best practices. The SAA-C03 exam includes:
- Updated service coverage
- New architectural patterns
- Enhanced security topics

Please refer to our new SAA-C03 guide for current exam preparation.',
    '<h1>AWS Solutions Architect SAA-C02 Exam Guide</h1><blockquote>Note: This exam has been retired...</blockquote>',
    'This guide covers the retired AWS SAA-C02 exam. Please see our updated SAA-C03 guide for current preparation.',
    'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800',
    1, 'ARCHIVED', '2024-06-15 10:00:00', 5420, 234, 67,
    6, 'AWS SAA-C02 Exam Guide (Retired)',
    'Historical guide for the retired AWS Solutions Architect SAA-C02 exam.',
    'AWS, SAA-C02, Solutions Architect, Retired Exam',
    '2024-01-10 09:00:00', '2025-08-01 12:00:00', 'admin', 'admin'
);

-- Post 7: PUBLISHED - CompTIA Security+ Guide
INSERT INTO posts (
    title, slug, content, content_html, excerpt, featured_image,
    author_id, status, published_at, views_count, likes_count, comments_count,
    reading_time_minutes, seo_title, seo_description, seo_keywords,
    created_at, updated_at, created_by, updated_by
) VALUES (
    'CompTIA Security+ SY0-701 Complete Study Guide',
    'comptia-security-plus-sy0-701-complete-study-guide',
    '# CompTIA Security+ SY0-701 Complete Study Guide

## About Security+

CompTIA Security+ is a globally recognized certification that validates baseline cybersecurity skills. The SY0-701 is the latest version of the exam.

## Exam Details

- **Exam Code**: SY0-701
- **Questions**: Maximum 90
- **Duration**: 90 minutes
- **Passing Score**: 750/900
- **Format**: Multiple choice and performance-based

## Domain Breakdown

### 1. General Security Concepts (12%)
- Security controls
- Fundamental security concepts
- Change management
- Cryptographic solutions

### 2. Threats, Vulnerabilities & Mitigations (22%)
- Threat actors and motivations
- Attack vectors
- Vulnerability types
- Mitigation techniques

### 3. Security Architecture (18%)
- Security implications of architecture models
- Secure enterprise infrastructure
- Data protection strategies

### 4. Security Operations (28%)
- Security monitoring
- Vulnerability management
- Incident response
- Digital forensics

### 5. Security Program Management (20%)
- Security governance
- Risk management
- Compliance requirements
- Security awareness

## Study Tips

1. Use multiple resources
2. Practice with labs
3. Take practice exams
4. Join study groups
5. Review exam objectives daily

## Recommended Resources

- CompTIA CertMaster
- Professor Messer videos
- CertiMaster practice exams
- Hands-on labs

Good luck on your Security+ journey!',
    '<h1>CompTIA Security+ SY0-701 Complete Study Guide</h1><h2>About Security+</h2><p>CompTIA Security+ is a globally recognized certification...</p>',
    'Complete preparation guide for CompTIA Security+ SY0-701 exam with domain breakdown, study tips, and recommended resources.',
    'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=800',
    3, 'PUBLISHED', '2025-12-25 08:00:00', 1680, 112, 31,
    9, 'CompTIA Security+ SY0-701 Study Guide 2025',
    'Comprehensive Security+ SY0-701 exam preparation guide with all domains covered and study strategies.',
    'CompTIA, Security+, SY0-701, Cybersecurity, IT Security Certification',
    '2025-12-23 15:00:00', '2025-12-25 08:00:00', 'admin', 'admin'
);

-- Post 8: PUBLISHED - Docker for Beginners
INSERT INTO posts (
    title, slug, content, content_html, excerpt, featured_image,
    author_id, status, published_at, views_count, likes_count, comments_count,
    reading_time_minutes, seo_title, seo_description, seo_keywords,
    created_at, updated_at, created_by, updated_by
) VALUES (
    'Docker for Beginners: A Practical Introduction',
    'docker-for-beginners-practical-introduction',
    '# Docker for Beginners: A Practical Introduction

## What is Docker?

Docker is a platform for developing, shipping, and running applications in containers. Containers package your application with all its dependencies.

## Why Use Docker?

- **Consistency**: Same environment everywhere
- **Isolation**: Applications run independently
- **Portability**: Run anywhere Docker is installed
- **Efficiency**: Lightweight compared to VMs

## Getting Started

### Installation

```bash
# Ubuntu
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io

# Verify installation
docker --version
```

### Your First Container

```bash
# Run hello-world
docker run hello-world

# Run nginx
docker run -d -p 80:80 nginx
```

## Essential Commands

```bash
# List containers
docker ps
docker ps -a

# List images
docker images

# Stop container
docker stop <container_id>

# Remove container
docker rm <container_id>

# Remove image
docker rmi <image_id>
```

## Dockerfile Basics

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 3000
CMD ["npm", "start"]
```

## Docker Compose

```yaml
version: "3.8"
services:
  web:
    build: .
    ports:
      - "3000:3000"
  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: secret
```

## Best Practices

1. Use official base images
2. Minimize layers
3. Use .dockerignore
4. Don''t run as root
5. Use multi-stage builds

Start containerizing your applications today!',
    '<h1>Docker for Beginners: A Practical Introduction</h1><h2>What is Docker?</h2><p>Docker is a platform for developing, shipping, and running applications in containers...</p>',
    'Learn Docker from scratch with this beginner-friendly guide covering installation, basic commands, Dockerfiles, and Docker Compose.',
    'https://images.unsplash.com/photo-1605745341112-85968b19335b?w=800',
    2, 'PUBLISHED', '2025-12-18 11:00:00', 3200, 198, 56,
    7, 'Docker Tutorial for Beginners 2025',
    'Complete Docker tutorial for beginners with practical examples, commands, and best practices.',
    'Docker, Containers, DevOps, Dockerfile, Docker Compose, Beginner',
    '2025-12-16 09:00:00', '2025-12-18 11:00:00', 'admin', 'admin'
);

-- =====================================================
-- POST-CATEGORY MAPPINGS
-- =====================================================
INSERT INTO post_category_mappings (post_id, category_id) VALUES
-- Post 1: AWS Solutions Architect
(1, 1),  -- AWS
-- Post 2: Kubernetes CKA
(2, 5),  -- Kubernetes
(2, 4),  -- DevOps
-- Post 3: DevOps CI/CD
(3, 4),  -- DevOps
-- Post 4: Azure AZ-104
(4, 2),  -- Azure
-- Post 5: Spring Boot Microservices
(5, 9),  -- Java
-- Post 6: AWS SAA-C02 (Archived)
(6, 1),  -- AWS
-- Post 7: CompTIA Security+
(7, 7),  -- Security
(7, 8),  -- CompTIA
-- Post 8: Docker Beginners
(8, 6),  -- Docker
(8, 4);  -- DevOps

-- =====================================================
-- POST-TAG MAPPINGS
-- =====================================================
INSERT INTO post_tag_mappings (post_id, tag_id) VALUES
-- Post 1: AWS Solutions Architect
(1, 1),  -- AWS Solutions Architect
(1, 41), -- Study Guide
(1, 42), -- Practice Exam
(1, 47), -- Beginner
-- Post 2: Kubernetes CKA
(2, 22), -- Kubernetes CKA
(2, 41), -- Study Guide
(2, 46), -- Hands-on Lab
(2, 48), -- Advanced
-- Post 3: DevOps CI/CD
(3, 15), -- CI/CD
(3, 17), -- GitHub Actions
(3, 45), -- Best Practices
(3, 46), -- Tutorial
-- Post 4: Azure AZ-104
(4, 11), -- Azure Administrator
(4, 41), -- Study Guide
(4, 47), -- Beginner
-- Post 5: Spring Boot Microservices
(5, 29), -- Spring Boot
(5, 30), -- Microservices
(5, 31), -- REST API
(5, 46), -- Tutorial
-- Post 6: AWS SAA-C02 (Archived)
(6, 1),  -- AWS Solutions Architect
(6, 41), -- Study Guide
-- Post 7: CompTIA Security+
(7, 25), -- Security+
(7, 41), -- Study Guide
(7, 42), -- Practice Exam
(7, 43), -- Career Advice
-- Post 8: Docker Beginners
(8, 21), -- Docker Compose
(8, 46), -- Tutorial
(8, 47), -- Beginner
(8, 45); -- Best Practices

-- =====================================================
-- COMMENTS (Sample comments for published posts)
-- =====================================================
INSERT INTO comments (post_id, user_id, parent_comment_id, content, likes_count, is_approved, created_at, created_by) VALUES
-- Comments on Post 1 (AWS SAA)
(1, 2, NULL, 'Great guide! This helped me pass my exam on the first try. The domain breakdown was especially useful.', 12, true, '2025-12-16 14:30:00', 'user2'),
(1, 3, NULL, 'Can you recommend any specific hands-on labs for the VPC section?', 5, true, '2025-12-17 09:15:00', 'user3'),
(1, 1, 2, 'Thanks for the feedback! For VPC labs, I recommend AWS Skill Builder and the official AWS workshops.', 8, true, '2025-12-17 10:00:00', 'admin'),
(1, 4, NULL, 'How long did you study before taking the exam?', 3, true, '2025-12-18 16:45:00', 'user4'),

-- Comments on Post 2 (Kubernetes CKA)
(2, 5, NULL, 'The kubectl aliases tip saved me so much time during the exam!', 15, true, '2025-12-21 11:00:00', 'user5'),
(2, 3, NULL, 'Is killer.sh really worth it for practice?', 7, true, '2025-12-22 08:30:00', 'user3'),
(2, 2, 6, 'Absolutely! The killer.sh simulator is very close to the real exam environment.', 9, true, '2025-12-22 09:45:00', 'user2'),

-- Comments on Post 3 (DevOps CI/CD)
(3, 4, NULL, 'This is exactly what I needed for setting up our team''s pipeline. Thanks!', 20, true, '2025-12-23 10:00:00', 'user4'),
(3, 5, NULL, 'Would love to see a follow-up article on GitOps practices.', 11, true, '2025-12-24 14:20:00', 'user5'),
(3, 1, 9, 'Great suggestion! I''m working on a GitOps article now. Stay tuned!', 6, true, '2025-12-24 15:00:00', 'admin'),

-- Comments on Post 7 (Security+)
(7, 2, NULL, 'The domain breakdown percentages are super helpful for prioritizing study time.', 8, true, '2025-12-26 09:00:00', 'user2'),
(7, 6, NULL, 'Just passed using this guide! Scored 812/900. Thank you!', 25, true, '2025-12-28 11:30:00', 'user6'),

-- Comments on Post 8 (Docker)
(8, 3, NULL, 'Finally a Docker tutorial that makes sense! The Dockerfile example was perfect.', 18, true, '2025-12-19 13:00:00', 'user3'),
(8, 4, NULL, 'Can you explain multi-stage builds in more detail?', 6, true, '2025-12-20 10:15:00', 'user4'),
(8, 2, 14, 'Multi-stage builds let you use multiple FROM statements. Each stage can copy artifacts from previous stages, resulting in smaller final images.', 10, true, '2025-12-20 11:00:00', 'user2');

-- =====================================================
-- POST REACTIONS (Sample reactions)
-- =====================================================
INSERT INTO post_reactions (post_id, user_id, reaction_type, created_at) VALUES
-- Reactions on Post 1
(1, 2, 'LIKE', '2025-12-16 14:35:00'),
(1, 3, 'HELPFUL', '2025-12-17 09:20:00'),
(1, 4, 'LIKE', '2025-12-18 16:50:00'),
(1, 5, 'LOVE', '2025-12-19 08:00:00'),
-- Reactions on Post 2
(2, 3, 'HELPFUL', '2025-12-21 11:05:00'),
(2, 4, 'LIKE', '2025-12-22 09:00:00'),
(2, 5, 'LOVE', '2025-12-22 14:30:00'),
-- Reactions on Post 3
(3, 2, 'HELPFUL', '2025-12-23 10:05:00'),
(3, 4, 'LOVE', '2025-12-24 14:25:00'),
(3, 5, 'LIKE', '2025-12-25 09:00:00'),
(3, 6, 'HELPFUL', '2025-12-26 11:00:00'),
-- Reactions on Post 7
(7, 2, 'LIKE', '2025-12-26 09:05:00'),
(7, 3, 'HELPFUL', '2025-12-27 10:00:00'),
(7, 6, 'LOVE', '2025-12-28 11:35:00'),
-- Reactions on Post 8
(8, 3, 'LOVE', '2025-12-19 13:05:00'),
(8, 4, 'HELPFUL', '2025-12-20 10:20:00'),
(8, 5, 'LIKE', '2025-12-21 08:00:00');

-- =====================================================
-- COMMENT REACTIONS (Sample likes on comments)
-- =====================================================
INSERT INTO comment_reactions (comment_id, user_id, created_at) VALUES
(1, 3, '2025-12-16 15:00:00'),
(1, 4, '2025-12-17 08:00:00'),
(1, 5, '2025-12-17 12:00:00'),
(3, 2, '2025-12-17 10:30:00'),
(3, 4, '2025-12-17 14:00:00'),
(5, 2, '2025-12-21 12:00:00'),
(5, 4, '2025-12-21 15:00:00'),
(7, 3, '2025-12-22 10:00:00'),
(8, 2, '2025-12-23 11:00:00'),
(12, 3, '2025-12-28 12:00:00'),
(12, 4, '2025-12-28 14:00:00'),
(13, 4, '2025-12-19 14:00:00'),
(15, 3, '2025-12-20 12:00:00');
