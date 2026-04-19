CREATE TABLE problem_categories (
    problem_id  UUID        NOT NULL,
    category    VARCHAR(50) NOT NULL,

    CONSTRAINT pk_problem_categories PRIMARY KEY (problem_id, category),
    CONSTRAINT fk_problem_categories_problem FOREIGN KEY (problem_id)
        REFERENCES problems(id),
    CONSTRAINT chk_problem_categories_category
        CHECK (category IN (
            'ARRAY', 'STRING', 'LINKED_LIST', 'TREE', 'GRAPH',
            'DYNAMIC_PROGRAMMING', 'BACKTRACKING', 'BINARY_SEARCH',
            'SLIDING_WINDOW', 'TWO_POINTERS', 'STACK', 'QUEUE',
            'HEAP', 'GREEDY', 'MATH', 'BIT_MANIPULATION', 'RECURSION'
        ))
);