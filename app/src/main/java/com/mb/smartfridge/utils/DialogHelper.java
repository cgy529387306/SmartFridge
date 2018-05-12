package com.mb.smartfridge.utils;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.mb.smartfridge.R;

/**
 * Dialog助手类
 * User: pcqpcq
 * Date: 13-9-4
 * Time: 上午9:20
 */
public class DialogHelper {

    /**
     * 创建对话框Builder
     *
     * @param context    上下文
     * @param icon       图标，为null时不显示
     * @param title      标题
     * @param message    显示信息
     * @param cancelable 是否可取消
     * @return 对话框Builder
     */
    public static AlertDialog.Builder createDialogBuilder(Context context, Drawable icon, String title, String message, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setCancelable(cancelable);
        if (icon!=null) {
            builder.setIcon(icon);
        }
        if (EmptyHelper.isNotEmpty(message)) {
            builder.setMessage(message);
        }
        return builder;
    }

    /**
     * 创建对话框Builder
     *
     * @param context    上下文
     * @param icon       图标，为0时不显示
     * @param title      标题
     * @param message    显示信息
     * @param cancelable 是否可取消
     * @return 对话框Builder
     */
    public static AlertDialog.Builder createDialogBuilder(Context context, int icon, int title, int message, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setCancelable(cancelable);
        if (icon != 0) {
            builder.setIcon(icon);
        }
        if (message != 0) {
            builder.setMessage(message);
        }
        return builder;
    }

    /**
     * 创建对话框Builder
     *
     * @param context    上下文
     * @param icon       图标，为0时不显示
     * @param title      标题
     * @param message    显示信息
     * @param cancelable 是否可取消
     * @return 对话框Builder
     */
    public static AlertDialog.Builder createDialogBuilder(Context context, int icon, String title, String message, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setCancelable(cancelable);
        if (icon != 0) {
            builder.setIcon(icon);
        }
        if (EmptyHelper.isNotEmpty(message)) {
            builder.setMessage(message);
        }
        return builder;
    }

    /**
     * 创建对话框Builder
     *
     * @param context            上下文
     * @param icon               对话框图标,为0则不显示
     * @param title              标题
     * @param message            显示信息
     * @param cancelable         是否可取消
     * @param positiveButtonText 确认按钮文字，空则使用默认
     * @param positiveListener   确认事件，null则只执行关闭对话框
     * @param negativeButtonText 否认按钮文字，空则使用默认
     * @param negativeListener   否认事件，null则只执行关闭对话框
     * @param neutralButtonText  中立按钮文字，空则不使用此按钮
     * @param neutralListener    中立事件，null则只执行关闭对话框
     */
    public static AlertDialog.Builder createDialogBuilder(
            Context context, int icon, String title, String message, boolean cancelable
            , String positiveButtonText, DialogInterface.OnClickListener positiveListener
            , String negativeButtonText, DialogInterface.OnClickListener negativeListener
            , String neutralButtonText, DialogInterface.OnClickListener neutralListener
    ) {
        AlertDialog.Builder builder = createDialogBuilder(context, icon, title, message, cancelable);
        //确认按钮
        String text = positiveButtonText;
        DialogInterface.OnClickListener listener = positiveListener;
        if (EmptyHelper.isEmpty(text)) {
            text = context.getString(android.R.string.ok);
        }
        if (listener==null) {
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        builder.setPositiveButton(text, listener);
        //否定按钮
        text = negativeButtonText;
        listener = negativeListener;
        if (EmptyHelper.isEmpty(text)) {
            text = context.getString(android.R.string.cancel);
        }
        if (listener==null) {
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        builder.setNegativeButton(text, listener);
        //中立按钮
        if (EmptyHelper.isNotEmpty(neutralButtonText)) {
            text = neutralButtonText;
            listener = neutralListener;
            if (listener==null) {
                listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
            }
            builder.setNeutralButton(text, listener);
        }

        return builder;
    }

    /**
     * 显示提示对话框（无图标，点击后关闭）
     *
     * @param context    上下文
     * @param title      标题
     * @param message    显示信息
     * @param cancelable 是否可取消
     */
    public static void showAlertDialog(Context context, String title, String message, boolean cancelable) {
        showAlertDialog(context, 0, title, message, cancelable, null);
    }

    /**
     * 显示提示对话框
     *
     * @param context    上下文
     * @param icon       对话框图标,为0则不显示
     * @param title      标题
     * @param message    显示信息
     * @param cancelable 是否可取消
     * @param listener   点击事件
     */
    public static void showAlertDialog(Context context, int icon, String title, String message, boolean cancelable, DialogInterface.OnClickListener listener) {
//		try{
        AlertDialog.Builder builder = createDialogBuilder(context, icon, title, message, cancelable);
        if (listener == null) {
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            builder.setPositiveButton(android.R.string.ok, listener);
        }
        builder.create().show();
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
    }

    /**
     * 显示确认对话框(两键,默认取消的行为只有关闭)
     *
     * @param context            上下文
     * @param icon               为0则无icon
     * @param title              标题
     * @param message            显示信息
     * @param cancelable         是否可取消
     * @param positiveButtonText 确认按钮文字，空则使用默认
     * @param positiveListener   确认事件，null则只执行关闭对话框
     */
    public static void showConfirmDialog(
            Context context, int icon, String title, String message, boolean cancelable
            , int positiveButtonText, DialogInterface.OnClickListener positiveListener) {
        showConfirmDialog(context, icon, title, message, cancelable
                , positiveButtonText, positiveListener
                , 0, null
                , 0, null
        );
    }

    /**
     * 显示确认对话框(两键，无icon)
     *
     * @param context            上下文
     * @param title              标题
     * @param message            显示信息
     * @param cancelable         是否可取消
     * @param positiveButtonText 确认按钮文字，空则使用默认
     * @param positiveListener   确认事件，null则只执行关闭对话框
     * @param negativeButtonText 否认按钮文字，空则使用默认
     * @param negativeListener   否认事件，null则只执行关闭对话框
     */
    public static void showConfirmDialog(
            Context context, String title, String message, boolean cancelable
            , int positiveButtonText, DialogInterface.OnClickListener positiveListener
            , int negativeButtonText, DialogInterface.OnClickListener negativeListener
    ) {
        showConfirmDialog(context, 0, title, message, cancelable
                , positiveButtonText, positiveListener
                , negativeButtonText, negativeListener
                , 0, null
        );
    }

    /**
     * 显示确认对话框
     *
     * @param context            上下文
     * @param icon               对话框图标,为0则不显示
     * @param title              标题
     * @param message            显示信息
     * @param cancelable         是否可取消
     * @param positiveButtonText 确认按钮文字，空则使用默认
     * @param positiveListener   确认事件，null则只执行关闭对话框
     * @param negativeButtonText 否认按钮文字，空则使用默认
     * @param negativeListener   否认事件，null则只执行关闭对话框
     * @param neutralButtonText  中立按钮文字，空则不使用此按钮
     * @param neutralListener    中立事件，null则只执行关闭对话框
     */
    public static void showConfirmDialog(
            Context context, int icon, String title, String message, boolean cancelable
            , int positiveButtonText, DialogInterface.OnClickListener positiveListener
            , int negativeButtonText, DialogInterface.OnClickListener negativeListener
            , int neutralButtonText, DialogInterface.OnClickListener neutralListener
    ) {
        showConfirmDialog(context, icon, title, message, cancelable
                , positiveButtonText == 0 ? null : context.getString(positiveButtonText), positiveListener
                , negativeButtonText == 0 ? null : context.getString(negativeButtonText), negativeListener
                , neutralButtonText == 0 ? null : context.getString(neutralButtonText), neutralListener
        );
    }

    /**
     * 显示确认对话框
     *
     * @param context            上下文
     * @param icon               对话框图标,为0则不显示
     * @param title              标题
     * @param message            显示信息
     * @param cancelable         是否可取消
     * @param positiveButtonText 确认按钮文字，空则使用默认
     * @param positiveListener   确认事件，null则只执行关闭对话框
     * @param negativeButtonText 否认按钮文字，空则使用默认
     * @param negativeListener   否认事件，null则只执行关闭对话框
     * @param neutralButtonText  中立按钮文字，空则不使用此按钮
     * @param neutralListener    中立事件，null则只执行关闭对话框
     */
    public static void showConfirmDialog(
            Context context, int icon, String title, String message, boolean cancelable
            , String positiveButtonText, DialogInterface.OnClickListener positiveListener
            , String negativeButtonText, DialogInterface.OnClickListener negativeListener
            , String neutralButtonText, DialogInterface.OnClickListener neutralListener
    ) {
        try {
            AlertDialog.Builder builder = createDialogBuilder(
                    context, icon, title, message, cancelable
                    , positiveButtonText, positiveListener
                    , negativeButtonText, negativeListener
                    , neutralButtonText, neutralListener
            );

            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义对话框 <br>
     * 默认显示位置在屏幕中间<br>
     * 默认宽度占屏幕宽度大小80％
     * @param context
     * @param contentView
     * @return
     */
    public static Dialog showCustomDialog(Context context, View contentView) {
        return showCustomDialog(context, contentView, Gravity.CENTER, 0.8f);
    }

    /**
     *  自定义对话框<br>
     *  默认开启触摸对话框外隐藏对话框<br>
     *  默认开启按返回键隐藏对话框
     * @param context
     * @param contentView 对话框显示内容
     * @param gravityPosition 显示位置
     * @param widthPer 显示大小百分比 （相对屏幕)
     * @return
     */
    public static Dialog showCustomDialog(Context context, View contentView, int gravityPosition, float widthPer) {
        return showCustomDialog(context, R.style.MB_Custom_Dialog, contentView, gravityPosition, widthPer, true, true);
    }

    /**
     *  自定义对话框
     * @param context
     * @param dialogTheme 对话框主题
     * @param contentView 对话框显示内容
     * @param gravityPosition 对话框显示位置
     * @param widthPer 对话框显示百分比（相对屏幕大小）
     * @param isCanceledOnTouchOutside 是否触摸对话框以外隐藏对话框
     * @param isCancelable 是否允许按返回键取消对话框
     * @return
     */
    public static Dialog showCustomDialog(Context context, int dialogTheme, View contentView, int gravityPosition, float widthPer, boolean isCanceledOnTouchOutside, boolean isCancelable) {
        Dialog dialog = new Dialog(context, dialogTheme);
        // 设置显示View
        if(null != contentView) {
            dialog.setContentView(contentView);
        }
        // 设置显示位置
        dialog.getWindow().setGravity(gravityPosition);
        // 设置是否触摸对话框以外隐藏对话框
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        // 设置是否允许按返回键隐藏对话框
        dialog.setCancelable(isCancelable);
        // 设置对话框大小
        DisplayMetrics displayMetrics = new DisplayMetrics();
        dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (displayMetrics.widthPixels*widthPer);

        dialog.show();
        return dialog;
    }

}
