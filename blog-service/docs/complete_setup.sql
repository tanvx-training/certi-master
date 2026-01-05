-- =====================================================
-- Blog Service Complete Database Setup
-- Combines schema creation and seed data
-- Run this file to set up a fresh blog service database
-- =====================================================

-- Include schema
\i schema.sql

-- Include seed data
\i seed_data.sql

-- Verify setup
SELECT 'Categories created: ' || COUNT(*) FROM post_categories;
SELECT 'Tags created: ' || COUNT(*) FROM post_tags;
