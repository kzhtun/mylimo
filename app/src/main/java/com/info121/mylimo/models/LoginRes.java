package com.info121.mylimo.models;

/**
 * Created by KZHTUN on 7/21/2017.
 */

public class LoginRes {
    private ValidatedriverResult validatedriverResult;


    public ValidatedriverResult getValidatedriverResult() {
        return validatedriverResult;
    }

    public void setValidatedriverResult(ValidatedriverResult validatedriverResult) {
        this.validatedriverResult = validatedriverResult;
    }

    public class ValidatedriverResult {

        public String duration;
        public String valid;


        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getValid() {
            return valid;
        }

        public void setValid(String valid) {
            this.valid = valid;
        }
    }
}
