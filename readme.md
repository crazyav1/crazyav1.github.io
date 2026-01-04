ITS8080 — Energy Informatics
Lecture Materials & Assignment Task Descriptions
1. Course Context

This project is part of the ITS8080 – Energy Informatics course.
The course focuses on applying data science, time series analysis, machine learning, and optimisation techniques to energy systems, with a strong emphasis on:

electricity demand analysis

renewable energy (PV) integration

forecasting

decision support and optimal control

The assignment builds a complete, reproducible pipeline from raw data to forecast-driven optimisation.

2. Lecture Materials Overview

The following lecture topics form the theoretical basis for the assignment tasks.

Lecture 1–2: Introduction & Data Science Foundations

Main topics

Energy informatics overview

Data science lifecycle

Problem formulation

Data understanding and data quality

Types of data (time series, tabular)

Used in tasks

Task 1 (Dataset understanding)

Task 2 (Data science lifecycle plan)

Lecture 4: Data Cleaning & Missing Data

Main topics

Missing data identification

Missing data mechanisms:

MCAR (Missing Completely at Random)

MAR (Missing at Random)

MNAR (Missing Not at Random)

Deletion vs imputation

Univariate vs multivariate imputation

Used in tasks

Task 4 (PV module missing data handling)

Lecture 5: Exploratory Data Analysis & Visualisation

Main topics

Time series plots

Histograms

Scatter plots

Ethical and informative visualisation

Labels, legends, scales

Used in tasks

Task 3 (Visualisation & summaries)

Task 5 (Exploratory analysis)

Lecture 6: Feature Engineering

Main topics

Feature creation from time stamps

Weather-based features

Transformations (log, normalization)

Outlier detection (IQR method)

Feature relevance and correlation analysis

Used in tasks

Task 5 (Feature engineering)

Lecture 7: Time Series Analysis

Main topics

Stationarity

Trend, seasonality, residuals

Classical additive decomposition

ACF and PACF

Autoregressive models

Used in tasks

Task 6 (Decomposition)

Task 7 (Statistical modelling)

Lecture 9: Forecasting & Validation

Main topics

Rolling-origin forecasting

Walk-forward validation

Direct vs recursive strategies

Error metrics:

RMSE

Normalised RMSE (NRMSE)

MAE

Used in tasks

Task 7 (Model validation)

Task 9 (Out-of-sample forecasting)

Lecture 10: Machine Learning for Time Series

Main topics

Supervised learning for forecasting

Lag-based features

Gradient boosting (XGBoost)

Model comparison with classical methods

Used in tasks

Task 8 (Machine learning)

Task 10 (Models with exogenous inputs)

Lecture 12 & 14: Decision Support & Optimisation

Main topics

Forecast-based decision making

Energy management systems (EMS)

Linear programming (LP)

Mixed-integer linear programming (MILP)

Constraints and feasibility

Cost optimisation

Used in tasks

Task 11 (Optimal control of storage)

3. Assignment Tasks — Original Descriptions
   Task 1 — Dataset Understanding

Goal

Understand the provided dataset

Identify variables, target, and structure

Key questions

What is the target variable?

What inputs are available?

What is the time resolution?

What is the final goal of the analysis?

Task 2 — Data Science Lifecycle (Project Planning)

Goal

Plan the full analysis using a data science lifecycle

Required

Create a project plan diagram

Identify where most effort is expected

Identify whether external data sources are needed

Task 3 — Visualisation & Statistical Summaries

Goal

Explore data visually and statistically

Required

Time series plots of demand, price, and PV

At least two different plot types

Identify the most informative visualisation

Task 4 — Missing Data Analysis (PV Modules)

Goal

Analyse and handle missing PV module data

Required

Identify missing values and inconsistencies

Identify missing data mechanisms

Apply at least three missing data handling methods

Compare results numerically and visually

Task 5 — Feature Engineering

Goal

Engineer and evaluate features related to demand and weather

Required

Data description with statistics and visuals

Distribution analysis and transformations

Creation of new time and weather features

Feature relevance ranking and explanation

Task 6 — Time Series Analysis

Goal

Decompose demand into components

Required

Classical additive decomposition

Identify strongest seasonal effects

Create typical demand profiles

Explain methodology

Task 7 — Statistical Modelling

Goal

Model demand using autoregressive methods

Required

Stationarise data

ACF and PACF analysis

Train two AR/ARMA-family models

Evaluate using NRMSE

Compare models

Task 8 — Machine Learning

Goal

Apply an advanced ML model for demand forecasting

Required

Train ML model (e.g. XGBoost)

Explain hyperparameters

Compare with statistical model

Task 9 — Out-of-Sample Forecasting

Goal

Produce a 7-day hourly demand forecast

Required

Rolling out-of-sample forecasting

Use statistical and ML models

Compare with baseline models

Task 10 — Models with Inputs (Exogenous Features)

Goal

Improve models using additional features

Required

Include exogenous inputs

Compare MAE and NRMSE

Quantify performance improvement

Task 11 — Optimal Control of Storage

Goal

Optimise home energy management using forecasts

Assumptions

Ideal forecasts for price, PV, and weather

Demand must be forecasted

Battery, PV, and grid constraints apply

Required

Forecast demand for next 24 hours

Compute optimal control

Compare PV_low and PV_high scenarios