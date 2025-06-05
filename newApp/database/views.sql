/* ====== ========== =========
    Total Salary Component per month and year
   ====== ========== ========= */
CREATE OR REPLACE VIEW
    viewTotal_salary_component_per_month_year
AS
    SELECT
        EXTRACT(Month FROM ss.posting_date) AS month,
        EXTRACT(Year FROM ss.posting_date) AS year,
        sd.salary_component,
        SUM(sd.amount) as total_amount
    FROM
            `tabSalary Slip` AS ss
            JOIN
            `tabSalary Detail` AS sd
            ON
            ss.name = sd.parent AND sd.parenttype = 'Salary Slip'
            GROUP BY
            salary_component,month, year
            ORDER BY
            month, year;

/* ====== ========== =========
    Total Salary Slip per month and year
   ====== ========== ========= */
CREATE OR REPLACE VIEW
    viewTotal_salary_slip_per_month_year
AS
    SELECT
        EXTRACT(Month FROM ss.posting_date) AS month,
        EXTRACT(Year FROM ss.posting_date) AS year,
        SUM(ss.net_pay) AS total_salary
    FROM
        `tabSalary Slip` AS ss
    GROUP BY month, year;

/* ====== ========== =========
    Total Salary Slip with total salary Component per month and year
   ====== ========== ========= */
CREATE OR REPLACE VIEW
    viewTotal_salary_slip_with_detail_per_month_year
AS
    SELECT
        vtss.month, vtss.year, vtss.total_salary,
        vtsc.salary_component, vtsc.total_amount
    FROM
        viewTotal_salary_slip_per_month_year as vtss
    LEFT JOIN
        viewTotal_salary_component_per_month_year as vtsc
    ON
        vtss.month = vtsc.month
    AND vtss.year = vtsc.year