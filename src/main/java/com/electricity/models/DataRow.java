package com.electricity.models;

import java.time.OffsetDateTime;

public record DataRow(
  OffsetDateTime timestamp,
  double pvMod1,
  double pvMod2,
  double pvMod3,
  double demand,
  double pv,
  double price,
  double temperature,
  double pressure,
  double cloudCover,
  double cloudCoverLow,
  double cloudCoverMid,
  double cloudCoverHigh,
  double windSpeed10m,
  Double shortwaveRadiation,        
  Double directRadiation,           
  Double diffuseRadiation,          
  Double directNormalIrradiance,    
  double dayMax
) {}