-- =====================================================
-- Seed Data Script for Exam Service
-- =====================================================
-- This script inserts sample data for testing and development
-- Includes: 3 Certifications, Topics, Questions, Exams, Tags
-- =====================================================

-- =====================================================
-- Insert Tags (10-15 common tags)
-- =====================================================
INSERT INTO tags (id, name) VALUES
('11111111-1111-1111-1111-111111111111', 'core-java'),
('11111111-1111-1111-1111-111111111112', 'spring-boot'),
('11111111-1111-1111-1111-111111111113', 'spring-security'),
('11111111-1111-1111-1111-111111111114', 'kubernetes-basics'),
('11111111-1111-1111-1111-111111111115', 'kubernetes-advanced'),
('11111111-1111-1111-1111-111111111116', 'networking'),
('11111111-1111-1111-1111-111111111117', 'security'),
('11111111-1111-1111-1111-111111111118', 'database'),
('11111111-1111-1111-1111-111111111119', 'oop'),
('11111111-1111-1111-1111-11111111111a', 'collections'),
('11111111-1111-1111-1111-11111111111b', 'concurrency'),
('11111111-1111-1111-1111-11111111111c', 'microservices'),
('11111111-1111-1111-1111-11111111111d', 'docker'),
('11111111-1111-1111-1111-11111111111e', 'cloud-native'),
('11111111-1111-1111-1111-11111111111f', 'rest-api');

-- =====================================================
-- Certification 1: Oracle Certified Associate (OCA)
-- =====================================================
INSERT INTO certifications (id, name, code, provider, description, level, duration_minutes, passing_score, total_questions, price, status) VALUES
('22222222-2222-2222-2222-222222222221', 
 'Oracle Certified Associate Java SE 11 Developer', 
 'OCA-JAVA-SE-11', 
 'Oracle', 
 'The Oracle Certified Associate Java SE 11 Developer certification validates foundational knowledge of Java programming and object-oriented concepts.',
 'BEGINNER',
 180,
 68.00,
 50,
 245.00,
 'ACTIVE');

-- Topics for OCA
INSERT INTO topics (id, certification_id, name, code, description, weight_percentage, order_index) VALUES
('33333333-3333-3333-3333-333333333311', '22222222-2222-2222-2222-222222222221', 'Java Basics', 'OCA-TOPIC-1', 'Understanding Java basics including data types, operators, and control flow', 20.00, 1),
('33333333-3333-3333-3333-333333333312', '22222222-2222-2222-2222-222222222221', 'Working with Java Data Types', 'OCA-TOPIC-2', 'Primitive types, wrapper classes, and String manipulation', 15.00, 2),
('33333333-3333-3333-3333-333333333313', '22222222-2222-2222-2222-222222222221', 'Using Operators and Decision Constructs', 'OCA-TOPIC-3', 'Operators, if-else, switch statements', 15.00, 3),
('33333333-3333-3333-3333-333333333314', '22222222-2222-2222-2222-222222222221', 'Creating and Using Arrays', 'OCA-TOPIC-4', 'Array declaration, initialization, and manipulation', 10.00, 4),
('33333333-3333-3333-3333-333333333315', '22222222-2222-2222-2222-222222222221', 'Using Loop Constructs', 'OCA-TOPIC-5', 'For, while, do-while loops and enhanced for loop', 10.00, 5),
('33333333-3333-3333-3333-333333333316', '22222222-2222-2222-2222-222222222221', 'Working with Methods and Encapsulation', 'OCA-TOPIC-6', 'Method declaration, access modifiers, encapsulation principles', 30.00, 6);

-- =====================================================
-- Certification 2: Spring Professional Certification
-- =====================================================
INSERT INTO certifications (id, name, code, provider, description, level, duration_minutes, passing_score, total_questions, price, status) VALUES
('22222222-2222-2222-2222-222222222222', 
 'Spring Professional Certification', 
 'SPRING-PRO-2024', 
 'VMware', 
 'The Spring Professional certification validates expertise in Spring Framework, Spring Boot, and enterprise application development.',
 'INTERMEDIATE',
 90,
 76.00,
 50,
 200.00,
 'ACTIVE');

-- Topics for Spring
INSERT INTO topics (id, certification_id, name, code, description, weight_percentage, order_index) VALUES
('33333333-3333-3333-3333-333333333321', '22222222-2222-2222-2222-222222222222', 'Container, Dependency, and IOC', 'SPRING-TOPIC-1', 'Spring container, dependency injection, and inversion of control', 25.00, 1),
('33333333-3333-3333-3333-333333333322', '22222222-2222-2222-2222-222222222222', 'Aspect Oriented Programming', 'SPRING-TOPIC-2', 'AOP concepts, pointcuts, advice, and aspects', 15.00, 2),
('33333333-3333-3333-3333-333333333323', '22222222-2222-2222-2222-222222222222', 'Data Management', 'SPRING-TOPIC-3', 'JDBC, transactions, JPA, and Spring Data', 20.00, 3),
('33333333-3333-3333-3333-333333333324', '22222222-2222-2222-2222-222222222222', 'Spring Boot', 'SPRING-TOPIC-4', 'Auto-configuration, starters, and Spring Boot features', 20.00, 4),
('33333333-3333-3333-3333-333333333325', '22222222-2222-2222-2222-222222222222', 'Spring MVC and REST', 'SPRING-TOPIC-5', 'Web applications, REST APIs, and Spring MVC', 15.00, 5),
('33333333-3333-3333-3333-333333333326', '22222222-2222-2222-2222-222222222222', 'Security', 'SPRING-TOPIC-6', 'Spring Security fundamentals and authentication', 5.00, 6);

-- =====================================================
-- Certification 3: Certified Kubernetes Administrator (CKA)
-- =====================================================
INSERT INTO certifications (id, name, code, provider, description, level, duration_minutes, passing_score, total_questions, price, status) VALUES
('22222222-2222-2222-2222-222222222223', 
 'Certified Kubernetes Administrator', 
 'CKA-2024', 
 'Cloud Native Computing Foundation', 
 'The CKA certification validates skills in Kubernetes administration, including installation, configuration, and management of Kubernetes clusters.',
 'ADVANCED',
 120,
 66.00,
 15,
 395.00,
 'ACTIVE');

-- Topics for CKA
INSERT INTO topics (id, certification_id, name, code, description, weight_percentage, order_index) VALUES
('33333333-3333-3333-3333-333333333331', '22222222-2222-2222-2222-222222222223', 'Cluster Architecture', 'CKA-TOPIC-1', 'Understanding Kubernetes architecture and components', 25.00, 1),
('33333333-3333-3333-3333-333333333332', '22222222-2222-2222-2222-222222222223', 'Workloads and Scheduling', 'CKA-TOPIC-2', 'Deployments, pods, and scheduling', 15.00, 2),
('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222223', 'Services and Networking', 'CKA-TOPIC-3', 'Services, ingress, and network policies', 20.00, 3),
('33333333-3333-3333-3333-333333333334', '22222222-2222-2222-2222-222222222223', 'Storage', 'CKA-TOPIC-4', 'Persistent volumes, storage classes', 10.00, 4),
('33333333-3333-3333-3333-333333333335', '22222222-2222-2222-2222-222222222223', 'Troubleshooting', 'CKA-TOPIC-5', 'Debugging and troubleshooting cluster issues', 30.00, 5);


-- =====================================================
-- Questions for OCA Certification (20-30 questions)
-- =====================================================

-- Java Basics Questions (Topic 1)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444411', '33333333-3333-3333-3333-333333333311', 'SINGLE_CHOICE', 
 'What is the correct way to declare a main method in Java?',
 'The main method must be public static void main(String[] args) to be recognized as the entry point of a Java application.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555511', '44444444-4444-4444-4444-444444444411', 'public void main(String[] args)', false, 1),
('55555555-5555-5555-5555-555555555512', '44444444-4444-4444-4444-444444444411', 'public static void main(String[] args)', true, 2),
('55555555-5555-5555-5555-555555555513', '44444444-4444-4444-4444-444444444411', 'static void main(String[] args)', false, 3),
('55555555-5555-5555-5555-555555555514', '44444444-4444-4444-4444-444444444411', 'public main(String[] args)', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444412', '33333333-3333-3333-3333-333333333311', 'SINGLE_CHOICE', 
 'Which of the following is NOT a valid Java identifier?',
 'Java identifiers cannot start with a digit. They must start with a letter, underscore, or dollar sign.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555521', '44444444-4444-4444-4444-444444444412', '_variable', false, 1),
('55555555-5555-5555-5555-555555555522', '44444444-4444-4444-4444-444444444412', '$amount', false, 2),
('55555555-5555-5555-5555-555555555523', '44444444-4444-4444-4444-444444444412', '2ndValue', true, 3),
('55555555-5555-5555-5555-555555555524', '44444444-4444-4444-4444-444444444412', 'myVariable', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444413', '33333333-3333-3333-3333-333333333311', 'TRUE_FALSE', 
 'Java is a platform-independent language.',
 'True. Java code is compiled to bytecode which can run on any platform with a JVM.',
 'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555531', '44444444-4444-4444-4444-444444444413', 'True', true, 1),
('55555555-5555-5555-5555-555555555532', '44444444-4444-4444-4444-444444444413', 'False', false, 2);

-- Data Types Questions (Topic 2)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444421', '33333333-3333-3333-3333-333333333312', 'SINGLE_CHOICE', 
 'What is the size of an int in Java?',
 'An int in Java is always 32 bits (4 bytes), regardless of the platform.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555541', '44444444-4444-4444-4444-444444444421', '16 bits', false, 1),
('55555555-5555-5555-5555-555555555542', '44444444-4444-4444-4444-444444444421', '32 bits', true, 2),
('55555555-5555-5555-5555-555555555543', '44444444-4444-4444-4444-444444444421', '64 bits', false, 3),
('55555555-5555-5555-5555-555555555544', '44444444-4444-4444-4444-444444444421', 'Platform dependent', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444422', '33333333-3333-3333-3333-333333333312', 'MULTIPLE_CHOICE', 
 'Which of the following are wrapper classes in Java? (Select all that apply)',
 'Integer, Double, and Boolean are wrapper classes for primitive types int, double, and boolean respectively.',
 'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555551', '44444444-4444-4444-4444-444444444422', 'Integer', true, 1),
('55555555-5555-5555-5555-555555555552', '44444444-4444-4444-4444-444444444422', 'String', false, 2),
('55555555-5555-5555-5555-555555555553', '44444444-4444-4444-4444-444444444422', 'Double', true, 3),
('55555555-5555-5555-5555-555555555554', '44444444-4444-4444-4444-444444444422', 'Boolean', true, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444423', '33333333-3333-3333-3333-333333333312', 'SINGLE_CHOICE', 
 'What is the default value of a boolean variable in Java?',
 'The default value of a boolean instance variable is false.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555561', '44444444-4444-4444-4444-444444444423', 'true', false, 1),
('55555555-5555-5555-5555-555555555562', '44444444-4444-4444-4444-444444444423', 'false', true, 2),
('55555555-5555-5555-5555-555555555563', '44444444-4444-4444-4444-444444444423', 'null', false, 3),
('55555555-5555-5555-5555-555555555564', '44444444-4444-4444-4444-444444444423', '0', false, 4);

-- Operators Questions (Topic 3)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444431', '33333333-3333-3333-3333-333333333313', 'SINGLE_CHOICE', 
 'What is the result of 10 % 3 in Java?',
 'The modulus operator % returns the remainder of division. 10 divided by 3 is 3 with remainder 1.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555571', '44444444-4444-4444-4444-444444444431', '3', false, 1),
('55555555-5555-5555-5555-555555555572', '44444444-4444-4444-4444-444444444431', '1', true, 2),
('55555555-5555-5555-5555-555555555573', '44444444-4444-4444-4444-444444444431', '0', false, 3),
('55555555-5555-5555-5555-555555555574', '44444444-4444-4444-4444-444444444431', '10', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444432', '33333333-3333-3333-3333-333333333313', 'SINGLE_CHOICE', 
 'What is the difference between == and equals() in Java?',
 '== compares references (memory addresses) while equals() compares the actual content of objects.',
 'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555581', '44444444-4444-4444-4444-444444444432', 'They are the same', false, 1),
('55555555-5555-5555-5555-555555555582', '44444444-4444-4444-4444-444444444432', '== compares references, equals() compares content', true, 2),
('55555555-5555-5555-5555-555555555583', '44444444-4444-4444-4444-444444444432', 'equals() is faster than ==', false, 3),
('55555555-5555-5555-5555-555555555584', '44444444-4444-4444-4444-444444444432', '== can only be used with primitives', false, 4);

-- Arrays Questions (Topic 4)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444441', '33333333-3333-3333-3333-333333333314', 'SINGLE_CHOICE', 
 'How do you declare an array of integers in Java?',
 'Arrays can be declared using int[] arrayName or int arrayName[], but int[] is preferred.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555591', '44444444-4444-4444-4444-444444444441', 'int array[]', false, 1),
('55555555-5555-5555-5555-555555555592', '44444444-4444-4444-4444-444444444441', 'int[] array', true, 2),
('55555555-5555-5555-5555-555555555593', '44444444-4444-4444-4444-444444444441', 'array int[]', false, 3),
('55555555-5555-5555-5555-555555555594', '44444444-4444-4444-4444-444444444441', 'int array()', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444442', '33333333-3333-3333-3333-333333333314', 'SINGLE_CHOICE', 
 'What is the index of the first element in a Java array?',
 'Java arrays are zero-indexed, meaning the first element is at index 0.',
 'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-5555555555a1', '44444444-4444-4444-4444-444444444442', '1', false, 1),
('55555555-5555-5555-5555-5555555555a2', '44444444-4444-4444-4444-444444444442', '0', true, 2),
('55555555-5555-5555-5555-5555555555a3', '44444444-4444-4444-4444-444444444442', '-1', false, 3),
('55555555-5555-5555-5555-5555555555a4', '44444444-4444-4444-4444-444444444442', 'Depends on array size', false, 4);

-- Loop Questions (Topic 5)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444451', '33333333-3333-3333-3333-333333333315', 'SINGLE_CHOICE', 
 'Which loop is guaranteed to execute at least once?',
 'The do-while loop checks the condition after executing the loop body, so it always runs at least once.',
 'MEDIUM', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-5555555555b1', '44444444-4444-4444-4444-444444444451', 'for loop', false, 1),
('55555555-5555-5555-5555-5555555555b2', '44444444-4444-4444-4444-444444444451', 'while loop', false, 2),
('55555555-5555-5555-5555-5555555555b3', '44444444-4444-4444-4444-444444444451', 'do-while loop', true, 3),
('55555555-5555-5555-5555-5555555555b4', '44444444-4444-4444-4444-444444444451', 'enhanced for loop', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444452', '33333333-3333-3333-3333-333333333315', 'MULTIPLE_CHOICE', 
 'Which statements can be used to exit a loop? (Select all that apply)',
 'break exits the loop immediately, return exits the method (and thus the loop), and continue skips to the next iteration.',
 'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-5555555555c1', '44444444-4444-4444-4444-444444444452', 'break', true, 1),
('55555555-5555-5555-5555-5555555555c2', '44444444-4444-4444-4444-444444444452', 'continue', false, 2),
('55555555-5555-5555-5555-5555555555c3', '44444444-4444-4444-4444-444444444452', 'return', true, 3),
('55555555-5555-5555-5555-5555555555c4', '44444444-4444-4444-4444-444444444452', 'exit', false, 4);

-- Methods and Encapsulation Questions (Topic 6)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444461', '33333333-3333-3333-3333-333333333316', 'SINGLE_CHOICE', 
 'Which access modifier makes a member accessible only within the same class?',
 'The private access modifier restricts access to only within the declaring class.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-5555555555d1', '44444444-4444-4444-4444-444444444461', 'public', false, 1),
('55555555-5555-5555-5555-5555555555d2', '44444444-4444-4444-4444-444444444461', 'private', true, 2),
('55555555-5555-5555-5555-5555555555d3', '44444444-4444-4444-4444-444444444461', 'protected', false, 3),
('55555555-5555-5555-5555-5555555555d4', '44444444-4444-4444-4444-444444444461', 'default', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444462', '33333333-3333-3333-3333-333333333316', 'SINGLE_CHOICE', 
 'What is method overloading?',
 'Method overloading allows multiple methods with the same name but different parameters in the same class.',
 'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-5555555555e1', '44444444-4444-4444-4444-444444444462', 'Multiple methods with same name and parameters', false, 1),
('55555555-5555-5555-5555-5555555555e2', '44444444-4444-4444-4444-444444444462', 'Multiple methods with same name but different parameters', true, 2),
('55555555-5555-5555-5555-5555555555e3', '44444444-4444-4444-4444-444444444462', 'Overriding a parent class method', false, 3),
('55555555-5555-5555-5555-5555555555e4', '44444444-4444-4444-4444-444444444462', 'Using static methods', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444463', '33333333-3333-3333-3333-333333333316', 'MULTIPLE_CHOICE', 
 'Which are principles of encapsulation? (Select all that apply)',
 'Encapsulation involves making fields private and providing public getter/setter methods to control access.',
 'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-5555555555f1', '44444444-4444-4444-4444-444444444463', 'Make fields private', true, 1),
('55555555-5555-5555-5555-5555555555f2', '44444444-4444-4444-4444-444444444463', 'Provide public getter/setter methods', true, 2),
('55555555-5555-5555-5555-5555555555f3', '44444444-4444-4444-4444-444444444463', 'Make all methods static', false, 3),
('55555555-5555-5555-5555-5555555555f4', '44444444-4444-4444-4444-444444444463', 'Use inheritance', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444464', '33333333-3333-3333-3333-333333333316', 'SINGLE_CHOICE', 
 'Can a method have the same name as the class?',
 'Yes, but it is not a constructor unless it has no return type. A method can have the same name as the class.',
 'HARD', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555g01', '44444444-4444-4444-4444-444444444464', 'No, never', false, 1),
('55555555-5555-5555-5555-555555555g02', '44444444-4444-4444-4444-444444444464', 'Yes, but it is not a constructor if it has a return type', true, 2),
('55555555-5555-5555-5555-555555555g03', '44444444-4444-4444-4444-444444444464', 'Only if it is static', false, 3),
('55555555-5555-5555-5555-555555555g04', '44444444-4444-4444-4444-444444444464', 'Only if it is private', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444465', '33333333-3333-3333-3333-333333333316', 'TRUE_FALSE', 
 'A static method can access instance variables directly.',
 'False. Static methods belong to the class and cannot access instance variables without an object reference.',
 'MEDIUM', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555g11', '44444444-4444-4444-4444-444444444465', 'True', false, 1),
('55555555-5555-5555-5555-555555555g12', '44444444-4444-4444-4444-444444444465', 'False', true, 2);

-- Additional OCA questions for variety
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444466', '33333333-3333-3333-3333-333333333311', 'SINGLE_CHOICE', 
 'What is the output of System.out.println(10 + 20 + "Java");?',
 'The expression is evaluated left to right. 10 + 20 = 30, then 30 + "Java" = "30Java".',
 'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555g21', '44444444-4444-4444-4444-444444444466', '1020Java', false, 1),
('55555555-5555-5555-5555-555555555g22', '44444444-4444-4444-4444-444444444466', '30Java', true, 2),
('55555555-5555-5555-5555-555555555g23', '44444444-4444-4444-4444-444444444466', 'Java30', false, 3),
('55555555-5555-5555-5555-555555555g24', '44444444-4444-4444-4444-444444444466', 'Compilation error', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444467', '33333333-3333-3333-3333-333333333312', 'SINGLE_CHOICE', 
 'Which statement about String is correct?',
 'Strings are immutable in Java, meaning once created, their value cannot be changed.',
 'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555g31', '44444444-4444-4444-4444-444444444467', 'Strings are mutable', false, 1),
('55555555-5555-5555-5555-555555555g32', '44444444-4444-4444-4444-444444444467', 'Strings are immutable', true, 2),
('55555555-5555-5555-5555-555555555g33', '44444444-4444-4444-4444-444444444467', 'Strings are primitive types', false, 3),
('55555555-5555-5555-5555-555555555g34', '44444444-4444-4444-4444-444444444467', 'Strings cannot be null', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444468', '33333333-3333-3333-3333-333333333314', 'SINGLE_CHOICE', 
 'What happens when you try to access an array element beyond its length?',
 'Accessing an array element beyond its bounds throws an ArrayIndexOutOfBoundsException at runtime.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555g41', '44444444-4444-4444-4444-444444444468', 'Returns null', false, 1),
('55555555-5555-5555-5555-555555555g42', '44444444-4444-4444-4444-444444444468', 'Returns 0', false, 2),
('55555555-5555-5555-5555-555555555g43', '44444444-4444-4444-4444-444444444468', 'Throws ArrayIndexOutOfBoundsException', true, 3),
('55555555-5555-5555-5555-555555555g44', '44444444-4444-4444-4444-444444444468', 'Compilation error', false, 4);


-- =====================================================
-- Questions for Spring Professional Certification (20-30 questions)
-- =====================================================

-- Container, Dependency, and IOC Questions (Topic 1)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444511', '33333333-3333-3333-3333-333333333321', 'SINGLE_CHOICE', 
 'What is Dependency Injection?',
 'Dependency Injection is a design pattern where dependencies are provided to an object rather than the object creating them itself.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555h11', '44444444-4444-4444-4444-444444444511', 'A way to create objects', false, 1),
('55555555-5555-5555-5555-555555555h12', '44444444-4444-4444-4444-444444444511', 'A pattern where dependencies are provided to an object', true, 2),
('55555555-5555-5555-5555-555555555h13', '44444444-4444-4444-4444-444444444511', 'A database connection pattern', false, 3),
('55555555-5555-5555-5555-555555555h14', '44444444-4444-4444-4444-444444444511', 'A security mechanism', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444512', '33333333-3333-3333-3333-333333333321', 'MULTIPLE_CHOICE', 
 'Which are valid ways to inject dependencies in Spring? (Select all that apply)',
 'Spring supports constructor injection, setter injection, and field injection.',
 'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555h21', '44444444-4444-4444-4444-444444444512', 'Constructor injection', true, 1),
('55555555-5555-5555-5555-555555555h22', '44444444-4444-4444-4444-444444444512', 'Setter injection', true, 2),
('55555555-5555-5555-5555-555555555h23', '44444444-4444-4444-4444-444444444512', 'Field injection', true, 3),
('55555555-5555-5555-5555-555555555h24', '44444444-4444-4444-4444-444444444512', 'Method injection', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444513', '33333333-3333-3333-3333-333333333321', 'SINGLE_CHOICE', 
 'What is the default scope of a Spring bean?',
 'The default scope is singleton, meaning one instance per Spring container.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555h31', '44444444-4444-4444-4444-444444444513', 'prototype', false, 1),
('55555555-5555-5555-5555-555555555h32', '44444444-4444-4444-4444-444444444513', 'singleton', true, 2),
('55555555-5555-5555-5555-555555555h33', '44444444-4444-4444-4444-444444444513', 'request', false, 3),
('55555555-5555-5555-5555-555555555h34', '44444444-4444-4444-4444-444444444513', 'session', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444514', '33333333-3333-3333-3333-333333333321', 'SINGLE_CHOICE', 
 'Which annotation is used to mark a class as a Spring bean?',
 '@Component is the generic stereotype annotation for any Spring-managed component.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555h41', '44444444-4444-4444-4444-444444444514', '@Bean', false, 1),
('55555555-5555-5555-5555-555555555h42', '44444444-4444-4444-4444-444444444514', '@Component', true, 2),
('55555555-5555-5555-5555-555555555h43', '44444444-4444-4444-4444-444444444514', '@Autowired', false, 3),
('55555555-5555-5555-5555-555555555h44', '44444444-4444-4444-4444-444444444514', '@Configuration', false, 4);

-- AOP Questions (Topic 2)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444521', '33333333-3333-3333-3333-333333333322', 'SINGLE_CHOICE', 
 'What is a pointcut in Spring AOP?',
 'A pointcut is an expression that selects one or more join points where advice should be applied.',
 'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555h51', '44444444-4444-4444-4444-444444444521', 'The code to be executed', false, 1),
('55555555-5555-5555-5555-555555555h52', '44444444-4444-4444-4444-444444444521', 'An expression that selects join points', true, 2),
('55555555-5555-5555-5555-555555555h53', '44444444-4444-4444-4444-444444444521', 'A Spring bean', false, 3),
('55555555-5555-5555-5555-555555555h54', '44444444-4444-4444-4444-444444444521', 'A database connection', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444522', '33333333-3333-3333-3333-333333333322', 'MULTIPLE_CHOICE', 
 'Which are types of advice in Spring AOP? (Select all that apply)',
 'Spring AOP supports @Before, @After, @Around, @AfterReturning, and @AfterThrowing advice.',
 'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555h61', '44444444-4444-4444-4444-444444444522', '@Before', true, 1),
('55555555-5555-5555-5555-555555555h62', '44444444-4444-4444-4444-444444444522', '@After', true, 2),
('55555555-5555-5555-5555-555555555h63', '44444444-4444-4444-4444-444444444522', '@Around', true, 3),
('55555555-5555-5555-5555-555555555h64', '44444444-4444-4444-4444-444444444522', '@During', false, 4);

-- Data Management Questions (Topic 3)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444531', '33333333-3333-3333-3333-333333333323', 'SINGLE_CHOICE', 
 'What annotation is used to mark a method as transactional?',
 '@Transactional marks a method or class to be executed within a transaction context.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555h71', '44444444-4444-4444-4444-444444444531', '@Transaction', false, 1),
('55555555-5555-5555-5555-555555555h72', '44444444-4444-4444-4444-444444444531', '@Transactional', true, 2),
('55555555-5555-5555-5555-555555555h73', '44444444-4444-4444-4444-444444444531', '@Tx', false, 3),
('55555555-5555-5555-5555-555555555h74', '44444444-4444-4444-4444-444444444531', '@Database', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444532', '33333333-3333-3333-3333-333333333323', 'SINGLE_CHOICE', 
 'What is the purpose of Spring Data JPA?',
 'Spring Data JPA simplifies data access by providing repository abstractions and reducing boilerplate code.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555h81', '44444444-4444-4444-4444-444444444532', 'To replace JPA', false, 1),
('55555555-5555-5555-5555-555555555h82', '44444444-4444-4444-4444-444444444532', 'To simplify data access with repository abstractions', true, 2),
('55555555-5555-5555-5555-555555555h83', '44444444-4444-4444-4444-444444444532', 'To create databases', false, 3),
('55555555-5555-5555-5555-555555555h84', '44444444-4444-4444-4444-444444444532', 'To handle security', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444533', '33333333-3333-3333-3333-333333333323', 'TRUE_FALSE', 
 'JpaRepository extends CrudRepository in Spring Data JPA.',
 'True. JpaRepository extends PagingAndSortingRepository which extends CrudRepository.',
 'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555h91', '44444444-4444-4444-4444-444444444533', 'True', true, 1),
('55555555-5555-5555-5555-555555555h92', '44444444-4444-4444-4444-444444444533', 'False', false, 2);

-- Spring Boot Questions (Topic 4)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444541', '33333333-3333-3333-3333-333333333324', 'SINGLE_CHOICE', 
 'What is the purpose of @SpringBootApplication annotation?',
 '@SpringBootApplication combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555ha1', '44444444-4444-4444-4444-444444444541', 'To enable security', false, 1),
('55555555-5555-5555-5555-555555555ha2', '44444444-4444-4444-4444-444444444541', 'To combine @Configuration, @EnableAutoConfiguration, and @ComponentScan', true, 2),
('55555555-5555-5555-5555-555555555ha3', '44444444-4444-4444-4444-444444444541', 'To create a database', false, 3),
('55555555-5555-5555-5555-555555555ha4', '44444444-4444-4444-4444-444444444541', 'To start a web server', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444542', '33333333-3333-3333-3333-333333333324', 'MULTIPLE_CHOICE', 
 'Which are benefits of Spring Boot? (Select all that apply)',
 'Spring Boot provides auto-configuration, embedded servers, and production-ready features.',
 'EASY', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hb1', '44444444-4444-4444-4444-444444444542', 'Auto-configuration', true, 1),
('55555555-5555-5555-5555-555555555hb2', '44444444-4444-4444-4444-444444444542', 'Embedded servers', true, 2),
('55555555-5555-5555-5555-555555555hb3', '44444444-4444-4444-4444-444444444542', 'Production-ready features', true, 3),
('55555555-5555-5555-5555-555555555hb4', '44444444-4444-4444-4444-444444444542', 'Replaces Spring Framework', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444543', '33333333-3333-3333-3333-333333333324', 'SINGLE_CHOICE', 
 'Where should you place application.properties in a Spring Boot project?',
 'application.properties should be placed in src/main/resources directory.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hc1', '44444444-4444-4444-4444-444444444543', 'src/main/java', false, 1),
('55555555-5555-5555-5555-555555555hc2', '44444444-4444-4444-4444-444444444543', 'src/main/resources', true, 2),
('55555555-5555-5555-5555-555555555hc3', '44444444-4444-4444-4444-444444444543', 'src/test/java', false, 3),
('55555555-5555-5555-5555-555555555hc4', '44444444-4444-4444-4444-444444444543', 'Root directory', false, 4);

-- Spring MVC and REST Questions (Topic 5)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444551', '33333333-3333-3333-3333-333333333325', 'SINGLE_CHOICE', 
 'Which annotation is used to create a RESTful controller?',
 '@RestController combines @Controller and @ResponseBody for REST APIs.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hd1', '44444444-4444-4444-4444-444444444551', '@Controller', false, 1),
('55555555-5555-5555-5555-555555555hd2', '44444444-4444-4444-4444-444444444551', '@RestController', true, 2),
('55555555-5555-5555-5555-555555555hd3', '44444444-4444-4444-4444-444444444551', '@Service', false, 3),
('55555555-5555-5555-5555-555555555hd4', '44444444-4444-4444-4444-444444444551', '@Repository', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444552', '33333333-3333-3333-3333-333333333325', 'MULTIPLE_CHOICE', 
 'Which HTTP methods are commonly used in REST APIs? (Select all that apply)',
 'REST APIs commonly use GET, POST, PUT, DELETE, and PATCH methods.',
 'EASY', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555he1', '44444444-4444-4444-4444-444444444552', 'GET', true, 1),
('55555555-5555-5555-5555-555555555he2', '44444444-4444-4444-4444-444444444552', 'POST', true, 2),
('55555555-5555-5555-5555-555555555he3', '44444444-4444-4444-4444-444444444552', 'PUT', true, 3),
('55555555-5555-5555-5555-555555555he4', '44444444-4444-4444-4444-444444444552', 'DELETE', true, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444553', '33333333-3333-3333-3333-333333333325', 'SINGLE_CHOICE', 
 'What annotation is used to bind a path variable in Spring MVC?',
 '@PathVariable is used to extract values from the URI path.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hf1', '44444444-4444-4444-4444-444444444553', '@RequestParam', false, 1),
('55555555-5555-5555-5555-555555555hf2', '44444444-4444-4444-4444-444444444553', '@PathVariable', true, 2),
('55555555-5555-5555-5555-555555555hf3', '44444444-4444-4444-4444-444444444553', '@RequestBody', false, 3),
('55555555-5555-5555-5555-555555555hf4', '44444444-4444-4444-4444-444444444553', '@ModelAttribute', false, 4);

-- Security Questions (Topic 6)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444561', '33333333-3333-3333-3333-333333333326', 'SINGLE_CHOICE', 
 'What is the purpose of Spring Security?',
 'Spring Security provides authentication, authorization, and protection against common security vulnerabilities.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hg1', '44444444-4444-4444-4444-444444444561', 'To create databases', false, 1),
('55555555-5555-5555-5555-555555555hg2', '44444444-4444-4444-4444-444444444561', 'To provide authentication and authorization', true, 2),
('55555555-5555-5555-5555-555555555hg3', '44444444-4444-4444-4444-444444444561', 'To handle HTTP requests', false, 3),
('55555555-5555-5555-5555-555555555hg4', '44444444-4444-4444-4444-444444444561', 'To manage transactions', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444562', '33333333-3333-3333-3333-333333333326', 'TRUE_FALSE', 
 'Spring Security uses filters to intercept requests.',
 'True. Spring Security is based on a chain of servlet filters.',
 'MEDIUM', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hh1', '44444444-4444-4444-4444-444444444562', 'True', true, 1),
('55555555-5555-5555-5555-555555555hh2', '44444444-4444-4444-4444-444444444562', 'False', false, 2);


-- =====================================================
-- Questions for CKA Certification (20-30 questions)
-- =====================================================

-- Cluster Architecture Questions (Topic 1)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444611', '33333333-3333-3333-3333-333333333331', 'SINGLE_CHOICE', 
 'What is the role of the kube-apiserver in Kubernetes?',
 'The kube-apiserver is the front-end for the Kubernetes control plane and exposes the Kubernetes API.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hi1', '44444444-4444-4444-4444-444444444611', 'Schedules pods', false, 1),
('55555555-5555-5555-5555-555555555hi2', '44444444-4444-4444-4444-444444444611', 'Exposes the Kubernetes API', true, 2),
('55555555-5555-5555-5555-555555555hi3', '44444444-4444-4444-4444-444444444611', 'Stores cluster data', false, 3),
('55555555-5555-5555-5555-555555555hi4', '44444444-4444-4444-4444-444444444611', 'Runs containers', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444612', '33333333-3333-3333-3333-333333333331', 'SINGLE_CHOICE', 
 'Which component stores all cluster data in Kubernetes?',
 'etcd is a consistent and highly-available key-value store used as Kubernetes backing store for all cluster data.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hj1', '44444444-4444-4444-4444-444444444612', 'kube-apiserver', false, 1),
('55555555-5555-5555-5555-555555555hj2', '44444444-4444-4444-4444-444444444612', 'etcd', true, 2),
('55555555-5555-5555-5555-555555555hj3', '44444444-4444-4444-4444-444444444612', 'kube-scheduler', false, 3),
('55555555-5555-5555-5555-555555555hj4', '44444444-4444-4444-4444-444444444612', 'kubelet', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444613', '33333333-3333-3333-3333-333333333331', 'MULTIPLE_CHOICE', 
 'Which are control plane components? (Select all that apply)',
 'Control plane components include kube-apiserver, etcd, kube-scheduler, and kube-controller-manager.',
 'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hk1', '44444444-4444-4444-4444-444444444613', 'kube-apiserver', true, 1),
('55555555-5555-5555-5555-555555555hk2', '44444444-4444-4444-4444-444444444613', 'etcd', true, 2),
('55555555-5555-5555-5555-555555555hk3', '44444444-4444-4444-4444-444444444613', 'kubelet', false, 3),
('55555555-5555-5555-5555-555555555hk4', '44444444-4444-4444-4444-444444444613', 'kube-scheduler', true, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444614', '33333333-3333-3333-3333-333333333331', 'TRUE_FALSE', 
 'The kubelet runs on every node in the cluster.',
 'True. The kubelet is the primary node agent that runs on each node.',
 'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hl1', '44444444-4444-4444-4444-444444444614', 'True', true, 1),
('55555555-5555-5555-5555-555555555hl2', '44444444-4444-4444-4444-444444444614', 'False', false, 2);

-- Workloads and Scheduling Questions (Topic 2)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444621', '33333333-3333-3333-3333-333333333332', 'SINGLE_CHOICE', 
 'What is a Pod in Kubernetes?',
 'A Pod is the smallest deployable unit in Kubernetes and can contain one or more containers.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hm1', '44444444-4444-4444-4444-444444444621', 'A single container', false, 1),
('55555555-5555-5555-5555-555555555hm2', '44444444-4444-4444-4444-444444444621', 'The smallest deployable unit containing one or more containers', true, 2),
('55555555-5555-5555-5555-555555555hm3', '44444444-4444-4444-4444-444444444621', 'A virtual machine', false, 3),
('55555555-5555-5555-5555-555555555hm4', '44444444-4444-4444-4444-444444444621', 'A cluster node', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444622', '33333333-3333-3333-3333-333333333332', 'SINGLE_CHOICE', 
 'What is the purpose of a Deployment in Kubernetes?',
 'A Deployment provides declarative updates for Pods and ReplicaSets.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hn1', '44444444-4444-4444-4444-444444444622', 'To store data', false, 1),
('55555555-5555-5555-5555-555555555hn2', '44444444-4444-4444-4444-444444444622', 'To provide declarative updates for Pods and ReplicaSets', true, 2),
('55555555-5555-5555-5555-555555555hn3', '44444444-4444-4444-4444-444444444622', 'To expose services', false, 3),
('55555555-5555-5555-5555-555555555hn4', '44444444-4444-4444-4444-444444444622', 'To manage secrets', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444623', '33333333-3333-3333-3333-333333333332', 'MULTIPLE_CHOICE', 
 'Which workload resources are available in Kubernetes? (Select all that apply)',
 'Kubernetes provides Deployment, StatefulSet, DaemonSet, and Job as workload resources.',
 'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555ho1', '44444444-4444-4444-4444-444444444623', 'Deployment', true, 1),
('55555555-5555-5555-5555-555555555ho2', '44444444-4444-4444-4444-444444444623', 'StatefulSet', true, 2),
('55555555-5555-5555-5555-555555555ho3', '44444444-4444-4444-4444-444444444623', 'DaemonSet', true, 3),
('55555555-5555-5555-5555-555555555ho4', '44444444-4444-4444-4444-444444444623', 'Service', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444624', '33333333-3333-3333-3333-333333333332', 'SINGLE_CHOICE', 
 'What does a DaemonSet ensure?',
 'A DaemonSet ensures that all (or some) nodes run a copy of a Pod.',
 'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hp1', '44444444-4444-4444-4444-444444444624', 'Pods are scheduled randomly', false, 1),
('55555555-5555-5555-5555-555555555hp2', '44444444-4444-4444-4444-444444444624', 'All nodes run a copy of a Pod', true, 2),
('55555555-5555-5555-5555-555555555hp3', '44444444-4444-4444-4444-444444444624', 'Only one Pod runs in the cluster', false, 3),
('55555555-5555-5555-5555-555555555hp4', '44444444-4444-4444-4444-444444444624', 'Pods are never restarted', false, 4);

-- Services and Networking Questions (Topic 3)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444631', '33333333-3333-3333-3333-333333333333', 'SINGLE_CHOICE', 
 'What is the purpose of a Service in Kubernetes?',
 'A Service exposes a set of Pods as a network service with a stable IP address.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hq1', '44444444-4444-4444-4444-444444444631', 'To store configuration', false, 1),
('55555555-5555-5555-5555-555555555hq2', '44444444-4444-4444-4444-444444444631', 'To expose Pods as a network service', true, 2),
('55555555-5555-5555-5555-555555555hq3', '44444444-4444-4444-4444-444444444631', 'To schedule Pods', false, 3),
('55555555-5555-5555-5555-555555555hq4', '44444444-4444-4444-4444-444444444631', 'To manage storage', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444632', '33333333-3333-3333-3333-333333333333', 'MULTIPLE_CHOICE', 
 'Which are valid Service types in Kubernetes? (Select all that apply)',
 'Kubernetes supports ClusterIP, NodePort, LoadBalancer, and ExternalName service types.',
 'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hr1', '44444444-4444-4444-4444-444444444632', 'ClusterIP', true, 1),
('55555555-5555-5555-5555-555555555hr2', '44444444-4444-4444-4444-444444444632', 'NodePort', true, 2),
('55555555-5555-5555-5555-555555555hr3', '44444444-4444-4444-4444-444444444632', 'LoadBalancer', true, 3),
('55555555-5555-5555-5555-555555555hr4', '44444444-4444-4444-4444-444444444632', 'InternalIP', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444633', '33333333-3333-3333-3333-333333333333', 'SINGLE_CHOICE', 
 'What is an Ingress in Kubernetes?',
 'An Ingress manages external access to services, typically HTTP/HTTPS routing.',
 'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hs1', '44444444-4444-4444-4444-444444444633', 'A type of Pod', false, 1),
('55555555-5555-5555-5555-555555555hs2', '44444444-4444-4444-4444-444444444633', 'Manages external access to services', true, 2),
('55555555-5555-5555-5555-555555555hs3', '44444444-4444-4444-4444-444444444633', 'A storage volume', false, 3),
('55555555-5555-5555-5555-555555555hs4', '44444444-4444-4444-4444-444444444633', 'A network plugin', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444634', '33333333-3333-3333-3333-333333333333', 'TRUE_FALSE', 
 'Network Policies in Kubernetes control traffic flow at the IP address or port level.',
 'True. Network Policies specify how groups of pods can communicate with each other and other network endpoints.',
 'MEDIUM', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555ht1', '44444444-4444-4444-4444-444444444634', 'True', true, 1),
('55555555-5555-5555-5555-555555555ht2', '44444444-4444-4444-4444-444444444634', 'False', false, 2);

-- Storage Questions (Topic 4)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444641', '33333333-3333-3333-3333-333333333334', 'SINGLE_CHOICE', 
 'What is a PersistentVolume (PV) in Kubernetes?',
 'A PersistentVolume is a piece of storage in the cluster that has been provisioned by an administrator or dynamically.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hu1', '44444444-4444-4444-4444-444444444641', 'A temporary storage', false, 1),
('55555555-5555-5555-5555-555555555hu2', '44444444-4444-4444-4444-444444444641', 'A piece of storage provisioned in the cluster', true, 2),
('55555555-5555-5555-5555-555555555hu3', '44444444-4444-4444-4444-444444444641', 'A container image', false, 3),
('55555555-5555-5555-5555-555555555hu4', '44444444-4444-4444-4444-444444444641', 'A network interface', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444642', '33333333-3333-3333-3333-333333333334', 'SINGLE_CHOICE', 
 'What is the difference between a PersistentVolume and a PersistentVolumeClaim?',
 'A PV is the actual storage resource, while a PVC is a request for storage by a user.',
 'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hv1', '44444444-4444-4444-4444-444444444642', 'They are the same', false, 1),
('55555555-5555-5555-5555-555555555hv2', '44444444-4444-4444-4444-444444444642', 'PV is storage, PVC is a request for storage', true, 2),
('55555555-5555-5555-5555-555555555hv3', '44444444-4444-4444-4444-444444444642', 'PVC is larger than PV', false, 3),
('55555555-5555-5555-5555-555555555hv4', '44444444-4444-4444-4444-444444444642', 'PV is temporary, PVC is permanent', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444643', '33333333-3333-3333-3333-333333333334', 'MULTIPLE_CHOICE', 
 'Which access modes are supported by PersistentVolumes? (Select all that apply)',
 'PersistentVolumes support ReadWriteOnce, ReadOnlyMany, and ReadWriteMany access modes.',
 'MEDIUM', 2, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hw1', '44444444-4444-4444-4444-444444444643', 'ReadWriteOnce', true, 1),
('55555555-5555-5555-5555-555555555hw2', '44444444-4444-4444-4444-444444444643', 'ReadOnlyMany', true, 2),
('55555555-5555-5555-5555-555555555hw3', '44444444-4444-4444-4444-444444444643', 'ReadWriteMany', true, 3),
('55555555-5555-5555-5555-555555555hw4', '44444444-4444-4444-4444-444444444643', 'WriteOnlyOnce', false, 4);

-- Troubleshooting Questions (Topic 5)
INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444651', '33333333-3333-3333-3333-333333333335', 'SINGLE_CHOICE', 
 'Which command is used to view logs of a Pod?',
 'kubectl logs <pod-name> is used to view logs from a Pod.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hx1', '44444444-4444-4444-4444-444444444651', 'kubectl get logs', false, 1),
('55555555-5555-5555-5555-555555555hx2', '44444444-4444-4444-4444-444444444651', 'kubectl logs', true, 2),
('55555555-5555-5555-5555-555555555hx3', '44444444-4444-4444-4444-444444444651', 'kubectl describe logs', false, 3),
('55555555-5555-5555-5555-555555555hx4', '44444444-4444-4444-4444-444444444651', 'kubectl view logs', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444652', '33333333-3333-3333-3333-333333333335', 'SINGLE_CHOICE', 
 'Which command provides detailed information about a Kubernetes resource?',
 'kubectl describe <resource-type> <resource-name> provides detailed information.',
 'EASY', 1, 60);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hy1', '44444444-4444-4444-4444-444444444652', 'kubectl get', false, 1),
('55555555-5555-5555-5555-555555555hy2', '44444444-4444-4444-4444-444444444652', 'kubectl describe', true, 2),
('55555555-5555-5555-5555-555555555hy3', '44444444-4444-4444-4444-444444444652', 'kubectl logs', false, 3),
('55555555-5555-5555-5555-555555555hy4', '44444444-4444-4444-4444-444444444652', 'kubectl explain', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444653', '33333333-3333-3333-3333-333333333335', 'MULTIPLE_CHOICE', 
 'Which are common reasons for a Pod to be in CrashLoopBackOff state? (Select all that apply)',
 'CrashLoopBackOff can be caused by application errors, missing dependencies, or incorrect configuration.',
 'HARD', 2, 120);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555hz1', '44444444-4444-4444-4444-444444444653', 'Application error in the container', true, 1),
('55555555-5555-5555-5555-555555555hz2', '44444444-4444-4444-4444-444444444653', 'Missing dependencies', true, 2),
('55555555-5555-5555-5555-555555555hz3', '44444444-4444-4444-4444-444444444653', 'Incorrect configuration', true, 3),
('55555555-5555-5555-5555-555555555hz4', '44444444-4444-4444-4444-444444444653', 'Too many replicas', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444654', '33333333-3333-3333-3333-333333333335', 'SINGLE_CHOICE', 
 'How can you execute a command inside a running container?',
 'kubectl exec -it <pod-name> -- <command> executes a command inside a container.',
 'MEDIUM', 1, 90);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555i01', '44444444-4444-4444-4444-444444444654', 'kubectl run', false, 1),
('55555555-5555-5555-5555-555555555i02', '44444444-4444-4444-4444-444444444654', 'kubectl exec', true, 2),
('55555555-5555-5555-5555-555555555i03', '44444444-4444-4444-4444-444444444654', 'kubectl attach', false, 3),
('55555555-5555-5555-5555-555555555i04', '44444444-4444-4444-4444-444444444654', 'kubectl connect', false, 4);

INSERT INTO questions (id, topic_id, type, content, explanation, difficulty, points, time_limit_seconds) VALUES
('44444444-4444-4444-4444-444444444655', '33333333-3333-3333-3333-333333333335', 'TRUE_FALSE', 
 'The kubectl get events command can help troubleshoot cluster issues.',
 'True. Events provide information about what is happening in the cluster.',
 'EASY', 1, 45);

INSERT INTO question_options (id, question_id, content, is_correct, order_index) VALUES
('55555555-5555-5555-5555-555555555i11', '44444444-4444-4444-4444-444444444655', 'True', true, 1),
('55555555-5555-5555-5555-555555555i12', '44444444-4444-4444-4444-444444444655', 'False', false, 2);


-- =====================================================
-- Insert Question Tags (linking questions to tags)
-- =====================================================

-- OCA Question Tags
INSERT INTO question_tags (id, question_id, tag_id) VALUES
('66666666-6666-6666-6666-666666666611', '44444444-4444-4444-4444-444444444411', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666612', '44444444-4444-4444-4444-444444444412', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666613', '44444444-4444-4444-4444-444444444413', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666614', '44444444-4444-4444-4444-444444444421', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666615', '44444444-4444-4444-4444-444444444422', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666616', '44444444-4444-4444-4444-444444444423', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666617', '44444444-4444-4444-4444-444444444431', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666618', '44444444-4444-4444-4444-444444444432', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666619', '44444444-4444-4444-4444-444444444432', '11111111-1111-1111-1111-11111111111a'), -- collections
('66666666-6666-6666-6666-66666666661a', '44444444-4444-4444-4444-444444444441', '11111111-1111-1111-1111-11111111111a'), -- collections
('66666666-6666-6666-6666-66666666661b', '44444444-4444-4444-4444-444444444442', '11111111-1111-1111-1111-11111111111a'), -- collections
('66666666-6666-6666-6666-66666666661c', '44444444-4444-4444-4444-444444444451', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-66666666661d', '44444444-4444-4444-4444-444444444452', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-66666666661e', '44444444-4444-4444-4444-444444444461', '11111111-1111-1111-1111-111111111119'), -- oop
('66666666-6666-6666-6666-66666666661f', '44444444-4444-4444-4444-444444444462', '11111111-1111-1111-1111-111111111119'), -- oop
('66666666-6666-6666-6666-666666666620', '44444444-4444-4444-4444-444444444463', '11111111-1111-1111-1111-111111111119'), -- oop
('66666666-6666-6666-6666-666666666621', '44444444-4444-4444-4444-444444444464', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666622', '44444444-4444-4444-4444-444444444465', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666623', '44444444-4444-4444-4444-444444444466', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666624', '44444444-4444-4444-4444-444444444467', '11111111-1111-1111-1111-111111111111'), -- core-java
('66666666-6666-6666-6666-666666666625', '44444444-4444-4444-4444-444444444468', '11111111-1111-1111-1111-11111111111a'); -- collections

-- Spring Question Tags
INSERT INTO question_tags (id, question_id, tag_id) VALUES
('66666666-6666-6666-6666-666666666631', '44444444-4444-4444-4444-444444444511', '11111111-1111-1111-1111-111111111112'), -- spring-boot
('66666666-6666-6666-6666-666666666632', '44444444-4444-4444-4444-444444444512', '11111111-1111-1111-1111-111111111112'), -- spring-boot
('66666666-6666-6666-6666-666666666633', '44444444-4444-4444-4444-444444444513', '11111111-1111-1111-1111-111111111112'), -- spring-boot
('66666666-6666-6666-6666-666666666634', '44444444-4444-4444-4444-444444444514', '11111111-1111-1111-1111-111111111112'), -- spring-boot
('66666666-6666-6666-6666-666666666635', '44444444-4444-4444-4444-444444444521', '11111111-1111-1111-1111-111111111112'), -- spring-boot
('66666666-6666-6666-6666-666666666636', '44444444-4444-4444-4444-444444444522', '11111111-1111-1111-1111-111111111112'), -- spring-boot
('66666666-6666-6666-6666-666666666637', '44444444-4444-4444-4444-444444444531', '11111111-1111-1111-1111-111111111118'), -- database
('66666666-6666-6666-6666-666666666638', '44444444-4444-4444-4444-444444444532', '11111111-1111-1111-1111-111111111118'), -- database
('66666666-6666-6666-6666-666666666639', '44444444-4444-4444-4444-444444444533', '11111111-1111-1111-1111-111111111118'), -- database
('66666666-6666-6666-6666-66666666663a', '44444444-4444-4444-4444-444444444541', '11111111-1111-1111-1111-111111111112'), -- spring-boot
('66666666-6666-6666-6666-66666666663b', '44444444-4444-4444-4444-444444444542', '11111111-1111-1111-1111-111111111112'), -- spring-boot
('66666666-6666-6666-6666-66666666663c', '44444444-4444-4444-4444-444444444543', '11111111-1111-1111-1111-111111111112'), -- spring-boot
('66666666-6666-6666-6666-66666666663d', '44444444-4444-4444-4444-444444444551', '11111111-1111-1111-1111-11111111111f'), -- rest-api
('66666666-6666-6666-6666-66666666663e', '44444444-4444-4444-4444-444444444552', '11111111-1111-1111-1111-11111111111f'), -- rest-api
('66666666-6666-6666-6666-66666666663f', '44444444-4444-4444-4444-444444444553', '11111111-1111-1111-1111-11111111111f'), -- rest-api
('66666666-6666-6666-6666-666666666640', '44444444-4444-4444-4444-444444444561', '11111111-1111-1111-1111-111111111113'), -- spring-security
('66666666-6666-6666-6666-666666666641', '44444444-4444-4444-4444-444444444561', '11111111-1111-1111-1111-111111111117'), -- security
('66666666-6666-6666-6666-666666666642', '44444444-4444-4444-4444-444444444562', '11111111-1111-1111-1111-111111111113'), -- spring-security
('66666666-6666-6666-6666-666666666643', '44444444-4444-4444-4444-444444444562', '11111111-1111-1111-1111-111111111117'); -- security

-- CKA Question Tags
INSERT INTO question_tags (id, question_id, tag_id) VALUES
('66666666-6666-6666-6666-666666666651', '44444444-4444-4444-4444-444444444611', '11111111-1111-1111-1111-111111111114'), -- kubernetes-basics
('66666666-6666-6666-6666-666666666652', '44444444-4444-4444-4444-444444444612', '11111111-1111-1111-1111-111111111114'), -- kubernetes-basics
('66666666-6666-6666-6666-666666666653', '44444444-4444-4444-4444-444444444613', '11111111-1111-1111-1111-111111111114'), -- kubernetes-basics
('66666666-6666-6666-6666-666666666654', '44444444-4444-4444-4444-444444444614', '11111111-1111-1111-1111-111111111114'), -- kubernetes-basics
('66666666-6666-6666-6666-666666666655', '44444444-4444-4444-4444-444444444621', '11111111-1111-1111-1111-111111111114'), -- kubernetes-basics
('66666666-6666-6666-6666-666666666656', '44444444-4444-4444-4444-444444444622', '11111111-1111-1111-1111-111111111114'), -- kubernetes-basics
('66666666-6666-6666-6666-666666666657', '44444444-4444-4444-4444-444444444623', '11111111-1111-1111-1111-111111111115'), -- kubernetes-advanced
('66666666-6666-6666-6666-666666666658', '44444444-4444-4444-4444-444444444624', '11111111-1111-1111-1111-111111111115'), -- kubernetes-advanced
('66666666-6666-6666-6666-666666666659', '44444444-4444-4444-4444-444444444631', '11111111-1111-1111-1111-111111111116'), -- networking
('66666666-6666-6666-6666-66666666665a', '44444444-4444-4444-4444-444444444632', '11111111-1111-1111-1111-111111111116'), -- networking
('66666666-6666-6666-6666-66666666665b', '44444444-4444-4444-4444-444444444633', '11111111-1111-1111-1111-111111111116'), -- networking
('66666666-6666-6666-6666-66666666665c', '44444444-4444-4444-4444-444444444634', '11111111-1111-1111-1111-111111111116'), -- networking
('66666666-6666-6666-6666-66666666665d', '44444444-4444-4444-4444-444444444634', '11111111-1111-1111-1111-111111111117'), -- security
('66666666-6666-6666-6666-66666666665e', '44444444-4444-4444-4444-444444444641', '11111111-1111-1111-1111-111111111115'), -- kubernetes-advanced
('66666666-6666-6666-6666-66666666665f', '44444444-4444-4444-4444-444444444642', '11111111-1111-1111-1111-111111111115'), -- kubernetes-advanced
('66666666-6666-6666-6666-666666666660', '44444444-4444-4444-4444-444444444643', '11111111-1111-1111-1111-111111111115'), -- kubernetes-advanced
('66666666-6666-6666-6666-666666666661', '44444444-4444-4444-4444-444444444651', '11111111-1111-1111-1111-111111111114'), -- kubernetes-basics
('66666666-6666-6666-6666-666666666662', '44444444-4444-4444-4444-444444444652', '11111111-1111-1111-1111-111111111114'), -- kubernetes-basics
('66666666-6666-6666-6666-666666666663', '44444444-4444-4444-4444-444444444653', '11111111-1111-1111-1111-111111111115'), -- kubernetes-advanced
('66666666-6666-6666-6666-666666666664', '44444444-4444-4444-4444-444444444654', '11111111-1111-1111-1111-111111111114'), -- kubernetes-basics
('66666666-6666-6666-6666-666666666665', '44444444-4444-4444-4444-444444444655', '11111111-1111-1111-1111-111111111114'); -- kubernetes-basics

-- =====================================================
-- Insert Exams (2-3 per certification)
-- =====================================================

-- OCA Exams
INSERT INTO exams (id, certification_id, title, type, description, duration_minutes, total_questions, passing_score, status) VALUES
('77777777-7777-7777-7777-777777777711', '22222222-2222-2222-2222-222222222221', 
 'OCA Java SE 11 Practice Test 1', 'PRACTICE', 
 'Practice test covering all OCA Java SE 11 topics', 120, 20, 65, 'ACTIVE'),
('77777777-7777-7777-7777-777777777712', '22222222-2222-2222-2222-222222222221', 
 'OCA Java SE 11 Mock Exam', 'MOCK', 
 'Full-length mock exam simulating the actual OCA exam', 180, 50, 68, 'ACTIVE'),
('77777777-7777-7777-7777-777777777713', '22222222-2222-2222-2222-222222222221', 
 'OCA Java SE 11 Final Exam', 'FINAL', 
 'Official OCA Java SE 11 certification exam', 180, 50, 68, 'ACTIVE');

-- Spring Exams
INSERT INTO exams (id, certification_id, title, type, description, duration_minutes, total_questions, passing_score, status) VALUES
('77777777-7777-7777-7777-777777777721', '22222222-2222-2222-2222-222222222222', 
 'Spring Professional Practice Test', 'PRACTICE', 
 'Practice test for Spring Professional certification', 60, 15, 70, 'ACTIVE'),
('77777777-7777-7777-7777-777777777722', '22222222-2222-2222-2222-222222222222', 
 'Spring Professional Mock Exam', 'MOCK', 
 'Full-length mock exam for Spring Professional', 90, 50, 76, 'ACTIVE'),
('77777777-7777-7777-7777-777777777723', '22222222-2222-2222-2222-222222222222', 
 'Spring Professional Final Exam', 'FINAL', 
 'Official Spring Professional certification exam', 90, 50, 76, 'ACTIVE');

-- CKA Exams
INSERT INTO exams (id, certification_id, title, type, description, duration_minutes, total_questions, passing_score, status) VALUES
('77777777-7777-7777-7777-777777777731', '22222222-2222-2222-2222-222222222223', 
 'CKA Practice Exam', 'PRACTICE', 
 'Practice exam for Certified Kubernetes Administrator', 60, 10, 60, 'ACTIVE'),
('77777777-7777-7777-7777-777777777732', '22222222-2222-2222-2222-222222222223', 
 'CKA Mock Exam', 'MOCK', 
 'Full-length mock exam for CKA certification', 120, 15, 66, 'ACTIVE'),
('77777777-7777-7777-7777-777777777733', '22222222-2222-2222-2222-222222222223', 
 'CKA Final Exam', 'FINAL', 
 'Official CKA certification exam', 120, 15, 66, 'ACTIVE');

-- =====================================================
-- Insert Exam Questions (linking exams to questions)
-- =====================================================

-- OCA Practice Test 1 (20 questions)
INSERT INTO exam_questions (id, exam_id, question_id, order_index) VALUES
('88888888-8888-8888-8888-888888888811', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444411', 1),
('88888888-8888-8888-8888-888888888812', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444412', 2),
('88888888-8888-8888-8888-888888888813', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444413', 3),
('88888888-8888-8888-8888-888888888814', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444421', 4),
('88888888-8888-8888-8888-888888888815', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444422', 5),
('88888888-8888-8888-8888-888888888816', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444423', 6),
('88888888-8888-8888-8888-888888888817', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444431', 7),
('88888888-8888-8888-8888-888888888818', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444432', 8),
('88888888-8888-8888-8888-888888888819', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444441', 9),
('88888888-8888-8888-8888-88888888881a', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444442', 10),
('88888888-8888-8888-8888-88888888881b', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444451', 11),
('88888888-8888-8888-8888-88888888881c', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444452', 12),
('88888888-8888-8888-8888-88888888881d', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444461', 13),
('88888888-8888-8888-8888-88888888881e', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444462', 14),
('88888888-8888-8888-8888-88888888881f', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444463', 15),
('88888888-8888-8888-8888-888888888820', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444464', 16),
('88888888-8888-8888-8888-888888888821', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444465', 17),
('88888888-8888-8888-8888-888888888822', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444466', 18),
('88888888-8888-8888-8888-888888888823', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444467', 19),
('88888888-8888-8888-8888-888888888824', '77777777-7777-7777-7777-777777777711', '44444444-4444-4444-4444-444444444468', 20);

-- Spring Practice Test (15 questions)
INSERT INTO exam_questions (id, exam_id, question_id, order_index) VALUES
('88888888-8888-8888-8888-888888888831', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444511', 1),
('88888888-8888-8888-8888-888888888832', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444512', 2),
('88888888-8888-8888-8888-888888888833', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444513', 3),
('88888888-8888-8888-8888-888888888834', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444514', 4),
('88888888-8888-8888-8888-888888888835', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444521', 5),
('88888888-8888-8888-8888-888888888836', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444522', 6),
('88888888-8888-8888-8888-888888888837', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444531', 7),
('88888888-8888-8888-8888-888888888838', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444532', 8),
('88888888-8888-8888-8888-888888888839', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444533', 9),
('88888888-8888-8888-8888-88888888883a', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444541', 10),
('88888888-8888-8888-8888-88888888883b', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444542', 11),
('88888888-8888-8888-8888-88888888883c', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444543', 12),
('88888888-8888-8888-8888-88888888883d', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444551', 13),
('88888888-8888-8888-8888-88888888883e', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444552', 14),
('88888888-8888-8888-8888-88888888883f', '77777777-7777-7777-7777-777777777721', '44444444-4444-4444-4444-444444444553', 15);

-- CKA Practice Exam (10 questions)
INSERT INTO exam_questions (id, exam_id, question_id, order_index) VALUES
('88888888-8888-8888-8888-888888888851', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444611', 1),
('88888888-8888-8888-8888-888888888852', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444612', 2),
('88888888-8888-8888-8888-888888888853', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444621', 3),
('88888888-8888-8888-8888-888888888854', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444622', 4),
('88888888-8888-8888-8888-888888888855', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444631', 5),
('88888888-8888-8888-8888-888888888856', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444632', 6),
('88888888-8888-8888-8888-888888888857', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444641', 7),
('88888888-8888-8888-8888-888888888858', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444642', 8),
('88888888-8888-8888-8888-888888888859', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444651', 9),
('88888888-8888-8888-8888-88888888885a', '77777777-7777-7777-7777-777777777731', '44444444-4444-4444-4444-444444444652', 10);

-- =====================================================
-- End of Seed Data
-- =====================================================
