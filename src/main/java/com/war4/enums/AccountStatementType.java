package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账户流水类型
 * Created by hh on 2017.10.21 0021.
 */
@Getter
@AllArgsConstructor
public enum AccountStatementType {
    CASH_APPLY(0,"申请提现",AccountArithmetic.USABLE_MINUS_FREEZE_ADD,AccountType.BALANCE,"申请提现",""),
    CASH_SUCCESSFUL(1,"提现成功",AccountArithmetic.FREEZE_MINUS,AccountType.BALANCE,"提现成功",""),
    BID_REFUND(2,"一元夺票退款",AccountArithmetic.USABLE_ADD,AccountType.BALANCE,"一元夺票退款",""),
    CASH_REFUSE(3,"提现拒绝",AccountArithmetic.USABLE_ADD_FREEZE_MINUS,AccountType.BALANCE,"提现拒绝",""),
    AUCTION_CANCEL(4,"竞拍订单取消",AccountArithmetic.USABLE_ADD,AccountType.BALANCE,"竞拍出价方","毁约"),
    AUCTION_ORDER_REFUND(5,"竞拍订单退款",AccountArithmetic.USABLE_ADD,AccountType.BALANCE,"竞拍出局",""),
    VIDEO_CHAT_COST(6,"视频聊天扣费",AccountArithmetic.USABLE_MINUS,AccountType.BALANCE,"视频聊天支出",""),
    VIDEO_CHAT_PROFIT(7,"视频聊天利润",AccountArithmetic.USABLE_ADD,AccountType.BALANCE,"","视频聊天收入"),
    AUCTION_REWARD(8,"竞拍报酬",AccountArithmetic.USABLE_ADD,AccountType.BALANCE,"与","竞拍约会成功"),
    ORDER_PAY(9,"订单支付",AccountArithmetic.USABLE_MINUS,AccountType.BALANCE,"支付订单",""),
    RECHARGE(10,"充值成功",AccountArithmetic.USABLE_ADD,AccountType.BALANCE,"充值",""),
    GIVE_GIFT(11,"赠送礼物",AccountArithmetic.USABLE_MINUS,AccountType.BALANCE,"赠送","礼物"),
    GAIN_GIFT(12,"获得礼物",AccountArithmetic.USABLE_ADD,AccountType.BALANCE,"收到来自","送的礼物"),
    ;


    private Integer code;
    private String typeName;
    private AccountArithmetic arithmetic;//账户算法
    private AccountType accountType;//账户类型
    private String remarkPre;//前缀
    private String remarkSuf;//后缀

    public static AccountStatementType getByCode(Integer code){
        for (AccountStatementType accountStatementType : AccountStatementType.values()) {
            if (accountStatementType.getCode().equals(code)) {
                return accountStatementType;
            }
        }
        return null;
    }

}
