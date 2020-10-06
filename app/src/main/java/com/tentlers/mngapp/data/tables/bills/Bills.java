package com.tentlers.mngapp.data.tables.bills;

import com.tentlers.mngapp.data.tables.meters.AllMetersData;

import java.sql.Date;
import java.util.Formatter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Bills {
    @Ignore
    public final AllMetersData metersData;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo()
    public long billId;

    @ColumnInfo
    public int tenantId;

    @ColumnInfo
    public long initialMeterR;
    @ColumnInfo
    public long endMeterR;
    @ColumnInfo
    public java.util.Date createDate;
    @ColumnInfo
    public Date billpaymentDate;
    @ColumnInfo
    public boolean isBillPaid;
    @ColumnInfo
    public float electricCost;
    @ColumnInfo
    public float monthlycharge;
    @ColumnInfo
    public float additionalcharge;
    @ColumnInfo
    public float perUnitcoat;
    @ColumnInfo
    public float manuallyEnteredElectricCost;
    @ColumnInfo
    public float totalAmt;

    public Bills() {
        metersData = new AllMetersData();
    }

    @Ignore
    public boolean ismeterPay;/* to enter data in meter table if it is true.*/

    public void setEndMeterR(long endMeterR) {
        this.endMeterR = endMeterR;
        metersData.setLastMeterReading(endMeterR);
        ismeterPay = true;
    }

    public void setCreateDate() {
        this.createDate = new Date(System.currentTimeMillis());
        metersData.setDate(createDate);
    }

    public void setManuallyEnteredElectricCost(float manuallyEnteredElectricCost) {
        this.manuallyEnteredElectricCost = manuallyEnteredElectricCost;
        ismeterPay = false;
    }

    public void setTotalAmt() {
        this.totalAmt = getTotalAmt();
    }

    public float getTotalAmt() {
        return monthlycharge + additionalcharge + getMeteredElectricityCost() + manuallyEnteredElectricCost;
    }

    @Ignore
    public AllMetersData getMetersData() {
        return metersData;
    }

    @Ignore
    public float getMeteredElectricityCost() {
        return (endMeterR - initialMeterR) * perUnitcoat;
    }

    @Ignore
    public static String getBillDate(java.util.Date createDate) {
        Formatter formatter = new Formatter();
        return formatter.format("%tr , %ta, %td %tb, %tY", createDate, createDate, createDate, createDate, createDate).toString();
    }


}
