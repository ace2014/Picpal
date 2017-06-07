package com.pzl.picpal;

/**
 * Created by zl.peng on 2017/6/6 16:53.
 */

public class Constant {

    public static class Intent {

        public static final String INTENT_PHOTO_LIST = "intent_photo_list";

        public static final String INTENT_CURRENT_POSITION = "intent_current_position";

        public static final String INTENT_IS_EDIT_PHOTO = "intent_is_edit_photo";

        public static final String INTENT_RESULT_PHOTO_LIST = "intent_result_photo_list";

    }

    public static class Function {

        public static final String ADD_EDIT_PHOTO_OBJECT = "addEditPhoto";

    }

    public static class Code {

        public static final int OPEN_CAMERA = 0x101;

        public static final int PHOTO_PICKED_WITH_DATA = 0x105;

        public static final int RESULT_REQUEST_CODE = 0x201;

        //private static final int PERMISSIONS_REQUEST_CAMERA = 0x301;

        public static final int PERMISSIONS_REQUEST_CAMERA = 1;

    }

    public static class ParamType {

        public static final int COMPOSITE = 1;

        public static final int INTEGER = 2;

        public static final int FILE = 3;

        public static final int URI = 4;

        public static final int STRING = 5;
    }

}
