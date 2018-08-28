package com.ldx.landingpage.choosedialog.enums;

import java.util.Arrays;

public enum XIEPickType {
    CAMERA, GALLERY;

    public boolean inside(XIEPickType[] array) {
        return Arrays.asList(array).contains(this);
    }

    public static XIEPickType[] fromInt(int val) {
        if (val > values().length - 1){
            return new XIEPickType[] {CAMERA, GALLERY};
        }else{
            return new XIEPickType[] {values()[val]};
        }
    }
}
