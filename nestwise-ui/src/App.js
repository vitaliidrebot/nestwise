// src/App.js
import React from 'react';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import LoginPage from './components/LoginPage';
import HomePage from './components/HomePage';

function App() {
    return (
        <Router>
            <div className="App">
                <Routes>
                    <Route path="/home" element={<HomePage />} />
                    <Route path="/" element={<LoginPage />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;