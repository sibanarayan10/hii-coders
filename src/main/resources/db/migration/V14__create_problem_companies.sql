CREATE TABLE problem_companies (

    problem_id UUID NOT NULL,
    company VARCHAR(50) NOT NULL,

    CONSTRAINT pk_problem_companies
        PRIMARY KEY (problem_id, company),

    CONSTRAINT fk_problem_companies_problem
        FOREIGN KEY (problem_id)
        REFERENCES problems(id),

    CONSTRAINT chk_problem_companies
        CHECK (
            company IN (

                'GOOGLE',
                'MICROSOFT',
                'AMAZON',
                'META',
                'APPLE',
                'NETFLIX',
                'UBER',
                'ADOBE',
                'ORACLE',
                'IBM',
                'SALESFORCE',
                'LINKEDIN',
                'TWITTER',
                'SPOTIFY',
                'AIRBNB',
                'DROPBOX',
                'PAYPAL',
                'NVIDIA',
                'INTEL',
                'SAMSUNG',
                'TIKTOK',
                'ZOHO',
                'FLIPKART',
                'SWIGGY',
                'ZOMATO',
                'PHONEPE',
                'RAZORPAY',
                'PAYTM'
            )
        )
);