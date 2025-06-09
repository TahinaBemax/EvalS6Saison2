$(document).ready(function () {
    const selectedTotalSalarySlipYear = $("#year");

    let monthlySalaryChart = null;
    let salaryComponentsChart = null;

    init();

    function init() {
        fetchChartData();
        bindEvents();
    }


    function bindEvents() {
        selectedTotalSalarySlipYear.change(fetchChartData)
    }

    function fetchChartData() {
        const year = selectedTotalSalarySlipYear.val();

        $.ajax({
            url: "/salary-slips/total-per-month",
            method: "GET",
            data: {year : year || null},
            dataType: "json",
            beforeSend: function() {
                showLoading()
            },
            success: function(data) {
                if (data){
                    createMonthlySalaryChart(data);
                    createSalaryComponentsChart(data);
                    return true;
                }
                alert("An error occured when fetching data!")
            },
            error: function(xhr) {
                showXhrErrorMessage(xhr);
            },
            complete: function(){
                hideLoading()
            }
        });
    }

    /* ++++++ +++++ ++++++++
        HTML CONTENT
       ++++++ +++++ +++++++*/
    function createMonthlySalaryChart(data) {
        const ctx = document.getElementById('monthlySalaryChart').getContext('2d');
        
        // Destroy existing chart if it exists
        if (monthlySalaryChart) {
            monthlySalaryChart.destroy();
        }
        // Create array of all months
        const allMonths = Array.from({length: 12}, (_, i) => getMonthName(i + 1));
        
        // Initialize amounts array with zeros
        const amounts = Array(12).fill(0);
                
        // Fill in the actual amounts from data
        data.forEach(item => {
            amounts[item.month - 1] = item.totalAmount;
        });


        monthlySalaryChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: allMonths,
                datasets: [{
                    label: 'Monthly Total Salary',
                    data: amounts,
                    backgroundColor: 'rgba(54, 162, 235, 0.5)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return currencyFormat(value);
                            }
                        }
                    }
                }
            }
        });
    }

    function createSalaryComponentsChart(data) {
        const ctx = document.getElementById('salaryComponentsChart').getContext('2d');
        
        // Destroy existing chart if it exists
        if (salaryComponentsChart) {
            salaryComponentsChart.destroy();
        }

        // Create array for all months
        const allMonths = Array.from({length: 12}, (_, i) => getMonthName(i + 1));
        
        // Get unique components
        const components = new Set();
        data.forEach(month => {
            if (month.details) {
                month.details.forEach(detail => {
                    components.add(detail.salary_component);
                });
            }
        });

        // Create datasets for each component
        const datasets = Array.from(components).map((component, index) => {
            // Initialize monthly data for this component
            const monthlyData = Array(12).fill(0);
            
            // Fill in the actual data
            data.forEach(month => {
                if (month.details) {
                    const detail = month.details.find(d => d.salary_component === component);
                    if (detail) {
                        monthlyData[month.month - 1] = detail.total_amount;
                    }
                }
            });

            // Generate a unique color for each component
            const hue = (index * 137.5) % 360; // Golden angle approximation for good color distribution
            return {
                label: component,
                data: monthlyData,
                borderColor: `hsl(${hue}, 70%, 50%)`,
                backgroundColor: `hsla(${hue}, 70%, 50%, 0.1)`,
                borderWidth: 2,
                fill: false,
                tension: 0.4 // Makes the line slightly curved
            };
        });

        salaryComponentsChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: allMonths,
                datasets: datasets
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return currencyFormat(value);
                            }
                        },
                        grid: {
                            color: 'rgba(255, 255, 255, 0.1)'
                        }
                    },
                    x: {
                        grid: {
                            color: 'rgba(255, 255, 255, 0.1)'
                        }
                    }
                },
                plugins: {
                    title: {
                        display: true,
                        text: 'Salary Components Trend',
                        color: 'white',
                        font: {
                            size: 16
                        }
                    },
                    legend: {
                        labels: {
                            color: 'white'
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.dataset.label}: ${currencyFormat(context.raw)}`;
                            }
                        }
                    }
                },
                interaction: {
                    intersect: false,
                    mode: 'index'
                }
            }
        });
    }

    /* ++++++ +++++ ++++++++
        HELPER
        ++++++ +++++ +++++++*/

    function currencyFormat(amount){
        var currenyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'EUR'
        })

        return currenyFormatter.format(amount);
    }

    function getMonthName(monthNumber) {
        const months = [
            'January', 'February', 'March', 'April', 'May', 'June',
            'July', 'August', 'September', 'October', 'November', 'December'
        ];
        return months[monthNumber - 1] || '';
    }
});
