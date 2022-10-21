package com.example.architecture.messages;

import lombok.Data;

import java.text.DecimalFormat;

@Data
public class ExperimentResponse {
    int N0;
    double p0;

    double[][] ansSource;
    double[] ansDevice;
    DecimalFormat dF = new DecimalFormat( "#0.000000" );

    public ExperimentResponse(double[][] ansSource, double[] ansDevice, int N, double p0) {
        this.ansDevice = ansDevice;
        this.ansSource = ansSource;
        this.N0 = N;
        this.p0 = p0;
    }

    @Override
    public String toString() {
        return toStringSource() + "\n\n" + toStringDevice();
    }

    private String toStringDevice() {
        StringBuilder ans = new StringBuilder();
        ans.append("Device Characteristics:\n");
        ans.append("№\tKoeff\n");
        for (int i = 0; i < ansDevice.length; i++) {
//            ans.append("П").append(i).append("\t").append(ansDevice[i]).append("\n");
            ans.append("П").append(i).append("\t").append(dF.format(ansDevice[i]).replace(',', '.')).append("\n");
        }
        return ans.toString();
    }

    public String toStringSource() {
        StringBuilder ans = new StringBuilder();
        ans.append("Source Characteristics:\n");
        ans.append("№\tN\t\tP_отк\t\tТ_преб\t\tТ_БП\t\tТ_обсл\t\tД_БП\t\tД_обсл\n");
        for (int i = 0; i < ansSource.length; i++) {
            ans.append("И").append(i).append("\t").append(ansSource[i][0]).append("\t");
            for (int j = 1; j < ansSource[i].length; j++) {
//                ans.append(ansSource[i][j]).append("\t");
                ans.append(dF.format(ansSource[i][j]).replace(',', '.')).append("\t");
            }
            ans.append("\n");
        }
        return ans.toString();
    }

    public String getLabel() {
        return "N = " + N0 + "; p = " + p0;
    }
}
