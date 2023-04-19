package com.android04.capstonedesign.util;

import java.util.ArrayList;

// 저장한 위치 정보/앱 사용 정보 로그 데이터 전처리 클래스

public class LogPackingHelper {
    static double st_latitude = 37.6179164775;
    static double st_longitude = 127.068209;
    static double latitude_standard = 0.02252252252 / 40.0;
    static double longitude_standard = 0.01801801802 / 40.0;


    static public ArrayList<int[]> gps_packing(String[][] time_gps, String sex, String age){
        System.out.println("-------"+sex+age);
        ArrayList<int[]> final_result = new ArrayList<>();
        //0~6, 6~12, 12~18, 18~24
        int[] tmp_re = new int[32768];
        for (int i = 1; i < time_gps.length + 1; i++) {
            int hour = Integer.parseInt(time_gps[i-1][0].substring(0,2)) % 3;
            int minute = Integer.parseInt(time_gps[i-1][0].substring(2,4)) / 10;
            int result_ind = gps_change_to_list(time_gps[i-1][1], time_gps[i-1][2]);
            if (result_ind != -1){
                int ind = hour * (1600 * 6);
                ind += (minute*1600);
                tmp_re[result_ind + ind] = 1;
            }
            if (i == time_gps.length){
                break;
            }
            int next_hour = Integer.parseInt(time_gps[i][0].substring(0,2)) % 3;
            if ((hour) > (next_hour)){
                if (sex.equals("Male")){
                    tmp_re[32766] = 1;
                }else{
                    tmp_re[32766] = 2;
                }
                tmp_re[32767] = Integer.parseInt(age) - 19;
                final_result.add(tmp_re);

                //초기화
                tmp_re = new int[32768];
            }
        }
        //마지막 18~24
        if (sex.equals("Male")){
            tmp_re[32766] = 1;
        }else{
            tmp_re[32766] = 2;
        }
        tmp_re[32767] = Integer.parseInt(age) - 19;
        final_result.add(tmp_re);
        return final_result;
    }
    static public int gps_change_to_list(String la, String lo){
        double la_ = Double.parseDouble(la);
        double tmp_la = la_ - st_latitude;
        double lo_ = Double.parseDouble(lo);
        double tmp_lo = lo_ - st_longitude;
        if ((tmp_la < 0) | (tmp_lo < 0)){
            return -1;
        }
        int lo_ind = (int)(tmp_lo / longitude_standard);
        int la_ind = (int)(tmp_la / latitude_standard);
        if ((lo_ind > 41) | (la_ind > 41)){
            return -1;
        }
        int final_ind = la_ind +  (lo_ind * 40);
        return final_ind;
    }

    static public int[] application_packing(ArrayList<String[][]> time_application, String sex, String age){
        int[] final_result = new int[32768];
        System.out.println("-------"+sex+age);
        for (int i = 0; i < time_application.size(); i++) {
            int hour = Integer.parseInt(time_application.get(i)[0][0].substring(0,2));
            int minute = Integer.parseInt(time_application.get(i)[0][0].substring(2,4)) / 10;
            int application_check_length = time_application.get(i)[1].length;
            int st_ind = (6 * hour * application_check_length) + (minute * application_check_length);
            for (int j = 0; j < application_check_length; j++) {
                if (time_application.get(i)[1][j] != "-1"){
                    final_result[j+ st_ind] = Integer.parseInt(time_application.get(i)[1][j]);
                    final_result[32767-255+i] = 3;
                }

            }
        }
        if (sex.equals("Male")){
            final_result[32766] = 1;
        }else{
            final_result[32766] = 2;
        }
        final_result[32767] = Integer.parseInt(age) - 19;
        return final_result;
    }


}