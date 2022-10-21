package com.example.architecture.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SourceStat {
    String number;
    int countReq;
    double p_otk;
    double t_preb;
    double t_BP;
    double t_obsl;
    double d_BP;
    double d_obsl;
}
