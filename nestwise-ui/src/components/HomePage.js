import React, {useState} from 'react';
import {Line} from 'react-chartjs-2';
import 'chart.js/auto';
import 'chartjs-adapter-date-fns';

const HomePage = () => {
    const [exchangeRates, setExchangeRates] = useState([]);

    const fetchExchangeRates = async () => {
        try {
            const auth = localStorage.getItem('auth');
            const response = await fetch('http://localhost:8080/banks/privatbank/exchange-rates?currency=USD&from=2023-08-01&to=2024-11-01', {
                headers: {
                    'Authorization': `Basic ${auth}`
                }
            });
            const data = await response.json();
            setExchangeRates(data);
        } catch (error) {
            console.error('Error fetching exchange rates:', error);
        }
    };

    const chartData = {
        labels: exchangeRates.map(rate => rate.date),
        datasets: [
            {
                label: 'Sell Rate',
                data: exchangeRates.map(rate => rate.sellRate),
                borderColor: 'rgba(75, 192, 192, 1)',
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                fill: true,
            },
            {
                label: 'Buy Rate',
                data: exchangeRates.map(rate => rate.buyRate),
                borderColor: 'rgba(255, 99, 132, 1)',
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                fill: true,
            }
        ]
    };

    const chartOptions = {
        scales: {
            x: {
                type: 'time',
                time: {
                    unit: 'month',
                    tooltipFormat: 'yyyy-MM-dd',
                    displayFormats: {
                        month: 'yyyy-MM'
                    }
                }
            }
        },
        interaction: {
            mode: 'index',
            intersect: false,
        },
        plugins: {
            title: {
                display: true,
                text: 'Exchange rate: USD -> UAH',
                font: {
                    size: 20
                }
            }
        }
    };

    return (
        <div>
            <h2>Home Page</h2>
            <p>Welcome to the home page!</p>
            <button onClick={fetchExchangeRates}>Exchange rates</button>
            {exchangeRates.length > 0 && (
                <div>
                    <Line data={chartData} options={chartOptions} />
                </div>
            )}
        </div>
    );
};

export default HomePage;