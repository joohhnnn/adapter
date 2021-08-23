package com.txznet.adapter.module;

import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.BaseModule;
import com.txznet.adapter.tool.AirControlTool;
import com.txznet.sdk.TXZCarControlManager;
import com.txznet.sdk.TXZResourceManager;

import java.util.Random;

@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
public class AirControlModule extends BaseModule {

    private static AirControlModule instance;

    private AirControlModule() {
    }

    public static AirControlModule getInstance() {
        if (instance == null) {
            synchronized (AirControlModule.class) {
                if (instance == null)
                    instance = new AirControlModule();
            }
        }
        return instance;
    }

    @Override
    public void init() {
        //TODO 按需实现
//        TXZCarControlManager.getInstance().setACMgrTool(acMgrTool);
//        TXZCarControlManager.getInstance().setWSpeedDistance(AdpStatusManager.AIR_WIND_MIN, AdpStatusManager.AIR_WIND_MAX);
//        TXZCarControlManager.getInstance().setTEMPDistance(AdpStatusManager.AIR_TEMP_MIN, AdpStatusManager.AIR_TEMP_MAX);
    }

    //反馈语
    private String getText() {
        String[] speakText = new String[]{"好的", "遵命", "收到"};
        Random r = new Random();
        String text = speakText[r.nextInt(3)];
        return text;
    }

    //反馈语
    private String getText2() {
        String[] speakText = new String[]{"已打开", "好的", "已执行", "收到", "遵命", "没问题", "已切换"};
        Random r = new Random();
        String text = speakText[r.nextInt(7)];
        return text;
    }

    private TXZCarControlManager.ACMgrTool acMgrTool = new TXZCarControlManager.ACMgrTool() {


        /*
         * 打开空调
         */
        @Override
        public boolean openAirConditioner() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.openAirControl();
                        }
                    });
            return true;
        }

        /*
         * 关闭空调
         */
        @Override
        public boolean closeAirConditioner() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.closeAirControl();
                        }
                    });
            return true;
        }

        /*
         * 切换到外循环
         */
        @Override
        public boolean outLoop() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.switchLoopOutSide();
                        }
                    });
            return true;
        }

        /*
         * 切换到内循环
         */
        @Override
        public boolean innerLoop() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.switchLoopInSide();
                        }
                    });
            return true;
        }

        /*
         * 打开前除霜
         */
        @Override
        public boolean openFDef() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.openFrontDef();
                        }
                    });
            return true;
        }

        /*
         * 关闭前除霜
         */
        @Override
        public boolean closeFDef() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.closeFrontDef();
                        }
                    });
            return true;
        }

        /*
         * 打开后除霜
         */
        @Override
        public boolean openADef() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.openBehindDef();
                        }
                    });
            return true;
        }

        /*
         * 关闭后除霜
         */
        @Override
        public boolean closeADef() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.closeBehindDef();
                        }
                    });
            return true;
        }

        /*
         * 打开制冷
         */
        @Override
        public boolean openCompressor() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.openAC();
                        }
                    });

            return true;
        }

        /*
         * 关闭制冷
         */
        @Override
        public boolean closeCompressor() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    getText(), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.closeAC();
                        }
                    });
            return true;
        }

        /*
         * 切换模式
         */
        @Override
        public boolean selectMode(ACMode mode) {
            // 吹面模式
            if (mode == ACMode.MODE_BLOW_FACE) {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        getText2(), true, new Runnable() {
                            @Override
                            public void run() {
                                AirControlTool.switchModeFace();
                            }
                        });
            }
            // 吹脚模式
            else if (mode == ACMode.MODE_BLOW_FOOT) {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        getText(), true, new Runnable() {
                            @Override
                            public void run() {
                                AirControlTool.switchModeFoot();
                            }
                        });
            }
            // 除霜模式
            else if (mode == ACMode.MODE_DEFROST) {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        getText(), true, new Runnable() {
                            @Override
                            public void run() {
                                AirControlTool.switchModeDefrost();
                            }
                        });
            }
            // 自动模式
            else if (mode == ACMode.MODE_AUTO) {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        getText2(), true, new Runnable() {
                            @Override
                            public void run() {
                                AirControlTool.switchModeAuto();
                            }
                        });
            }
            // 吹面吹脚
            else if (mode == ACMode.MODE_BLOW_FACE_FOOT) {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        getText2(), true, new Runnable() {
                            @Override
                            public void run() {
                                AirControlTool.switchModeFaceFoot();
                            }
                        });
            }
            // 吹脚除霜
            else if (mode == ACMode.MODE_BLOW_FOOT_DEFROST) {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        getText(), true, new Runnable() {
                            @Override
                            public void run() {
                                AirControlTool.switchModeFootDefrost();
                            }
                        });
            }
            return true;
        }

        /*
         * 温度调到X
         */
        @Override
        public boolean ctrlToTemp(int temp) {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    "将为您调整温度至" + temp + "度", true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.ctrlTempTo(temp);
                        }
                    });
            return true;
        }

        /*
         * 提高温度
         */
        @Override
        public boolean incTemp() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    "已调至" +
                            (AdpStatusManager.getInstance().getCurAirTempInt() + 1) + "度", true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.incTemp(1);
                        }
                    });
            return true;
        }

        /*
         * 降低温度
         */
        @Override
        public boolean decTemp() {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    "已调至" +
                            (AdpStatusManager.getInstance().getCurAirTempInt() - 1) + "度", true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.decTemp(1);
                        }
                    });
            return true;
        }

        /*
         * 温度提高X
         */
        @Override
        public boolean incTemp(int temp) {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    "已调至" + (AdpStatusManager.getInstance().getCurAirTempInt() + temp) + "度", true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.incTemp(temp);
                        }
                    });
            return true;
        }

        /*
         * 温度降低X
         */
        @Override
        public boolean decTemp(int temp) {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    "已调至" + (AdpStatusManager.getInstance().getCurAirTempInt() - temp) + "度", true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.decTemp(temp);
                        }
                    });
            return true;
        }

        /*
         * 调到最高温度
         */
        @Override
        public boolean maxTemp() {
            TXZResourceManager.getInstance().speakTextOnRecordWin("温度已调至最高", true, new Runnable() {
                @Override
                public void run() {

                }
            });
            return true;
        }

        /*
         * 调到最低温度
         */
        @Override
        public boolean minTemp() {
            TXZResourceManager.getInstance().speakTextOnRecordWin("温度已调至最低", true, new Runnable() {
                @Override
                public void run() {

                }
            });
            return true;
        }

        /*
         * 风量调到X档
         */
        @Override
        public boolean ctrlToWSpeed(int speed) {
            TXZResourceManager.getInstance().speakTextOnRecordWin(
                    "已调至" + (AdpStatusManager.getInstance().getCurAirWindInt() + speed), true, new Runnable() {
                        @Override
                        public void run() {
                            AirControlTool.ctrlWindTo(speed);
                        }
                    });
            return true;
        }


        /*
         * 增大风速
         */
        @Override
        public boolean incWSpeed() {

            if (AdpStatusManager.getInstance().getCurAirWindInt() == AdpStatusManager.AIR_TEMP_MAX) {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        "风量已是最大", true, null);
            } else {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        "已调至" + (AdpStatusManager.getInstance().getCurAirWindInt() + 1), true, new Runnable() {
                            @Override
                            public void run() {
                                AirControlTool.incWind();
                            }
                        });
            }
            return true;
        }

        /*
         * 减小风速
         */
        @Override
        public boolean decWSpeed() {
            if (AdpStatusManager.getInstance().getCurAirWindInt() == AdpStatusManager.AIR_WIND_MIN) {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        "风量已是最小", true, null);
            } else {
                TXZResourceManager.getInstance().speakTextOnRecordWin(
                        "已调至" + (AdpStatusManager.getInstance().getCurAirWindInt() - 1), true, new Runnable() {
                            @Override
                            public void run() {
                                AirControlTool.decWind();
                            }
                        });
            }
            return true;
        }
    };
}
