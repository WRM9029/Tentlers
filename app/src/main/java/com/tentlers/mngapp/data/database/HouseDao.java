package com.tentlers.mngapp.data.database;

import com.tentlers.mngapp.data.tables.TableHouse;
import com.tentlers.mngapp.data.tables.TableRooms;
import com.tentlers.mngapp.data.tables.bills.BillItemForCard;
import com.tentlers.mngapp.data.tables.bills.Bills;
import com.tentlers.mngapp.data.tables.meters.AllMetersData;
import com.tentlers.mngapp.data.tables.queryobjects.HouseForHomeFragment;
import com.tentlers.mngapp.data.tables.queryobjects.HouseNameAndId;
import com.tentlers.mngapp.data.tables.queryobjects.HouseNameIdNoRooms;
import com.tentlers.mngapp.data.tables.queryobjects.HouseNameMeterId;
import com.tentlers.mngapp.data.tables.rooms.RoomNoName;
import com.tentlers.mngapp.data.tables.rooms.RoomNoNameId;
import com.tentlers.mngapp.data.tables.tenants.TenantBillEntry;
import com.tentlers.mngapp.data.tables.tenants.TenantNameHouseRoom;
import com.tentlers.mngapp.data.tables.tenants.TenantsPersonal;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public interface HouseDao {

    /*
     * For inserting new House
     */
    @Insert()
    void insertHouseRecord(TableHouse tableHouse);

    @Delete
    void deleteHouse(TableHouse tableHouse);

    @Query("DELETE FROM TableHouse WHERE houseId = :gotHouseId")
    void deleteHouseById(int gotHouseId);

    /* Deleting all the records related to the house.*/
    @Query("DELETE FROM tablerooms WHERE houseId = :houseId")
    void deleteRoomsOfTheHouse(int houseId);

    @Query("DELETE FROM tenantspersonal WHERE houseId = :houseId")
    void deleteTenantsOfTheHouse(int houseId);

    @Update
    void updateHouseData(TableHouse tableHouse);

    /*select all the house to display in the list on home fragment.*/
    @Query("SELECT houseId, houseName, noOfRooms,occupiedRooms,date FROM TableHouse ORDER BY houseId")
    LiveData<List<HouseForHomeFragment>> getAllHouseForHomeFragment();

    /*for displaying specific house.*/
    @Query("SELECT * FROM TableHouse WHERE houseId = :gotHouseid")
    LiveData<TableHouse> getHouseForSpecificHouse(int gotHouseid);

    @Query("SELECT houseName,meterid FROM TABLEHOUSE ORDER BY houseId DESC")
    LiveData<HouseNameMeterId[]> gethouseNameMeterId();

    /*
     * for updating no of rooms of the house
     */
    @Query("UPDATE tablehouse SET noOfRooms =noOfRooms + :increment  WHERE houseId = :houseid")
    void updateNoOfRoomsInTableHosue(int increment, int houseid);

    /*
     *For rooms
     */
    @Insert()
    void insetNewRoomRecord(TableRooms rooms);

    @Update
    void updateRoomData(TableRooms rooms);

    @Delete
    void deleteRoom(TableRooms rooms);

    @Query("SELECT * FROM TableRooms WHERE houseId = :givenhouseId ORDER BY date")
    LiveData<List<TableRooms>> getAllRoomsOfHouse(int givenhouseId);

    @Query("SELECT * FROM TableRooms  WHERE houseId = :houseidgot ORDER BY date LIMIT 3 ")
    LiveData<List<TableRooms>> getThreerooms(int houseidgot);

    /*
     *For displaying house names in the spinner of rooms fragment.
     */
    @Query("SELECT houseName , houseId, noOfRooms FROM TableHouse")
    LiveData<List<HouseNameIdNoRooms>> getHouseNameId();

    /*
     * For extracting all meter ids used for comparison while entering meterno for rooms.
     */
    @Query("SELECT meterid FROM tablerooms")
    LiveData<List<Long>> getRoomMeter();

    @Query("SELECT meterId FROM tablehouse")
    LiveData<List<Long>> getHouseMeterid();


    /*
     *For managing uniquness of room name in room entry fragment for a specific house.
     */
    @Query("SELECT roomNo,roomName FROM tablerooms WHERE houseId = :houseid ORDER BY roomNo DESC")
    LiveData<List<RoomNoName>> getroomNoName(long houseid);

    /*
     * For Tenants Personal Table
     */
    @Insert()
    void insertNewTenant(TenantsPersonal tenantsPersonal);

    @Update
    void updateTenant(TenantsPersonal tenantsPersonal);

    @Delete
    void deleteTenant(TenantsPersonal tenantsPersonal);

    /*for tenant entry fragment*/

    /*
     * For showing the eligible houses to which tenant can be added.
     * such having atleast one room available that can be added.
     */
    @Query("SELECT houseName,houseId FROM tablehouse WHERE noOfRooms >0 AND occupiedRooms < noOfRooms")
    LiveData<List<HouseNameAndId>> getHouseNameIdForTEspinner();

    /*
     * for showing the empty rooms in the spinner of tenant entry fragment
     */
    @Query("SELECT roomId,roomName,isMeterEnabled, meterId FROM tablerooms WHERE houseId = :thehouseId AND isOcupied = :gotOccupied")
    LiveData<List<RoomNoNameId>> getroomNoNameId(int thehouseId, boolean gotOccupied);

    /*
     * update number of occupied rooms.
     */
    @Query("UPDATE tablehouse SET occupiedRooms = occupiedRooms + :updaterooms WHERE houseId = :gothouseId")
    void updateNoOfEmptyRoomsInTable(int updaterooms, int gothouseId);

    /*
     * update the room status to occupied
     */
    @Query("UPDATE tablerooms SET isOcupied = :gotIsOccupied WHERE roomId = :gotRoomid")
    void updatetheRoomOccupiedStatus(boolean gotIsOccupied, int gotRoomid);


    /* Tenant fragment.
     * Getting the all tenant information.
     * This is can handle both the querries for currently active tenant and previously active tenant.
     * in the houses
     */
    @Query("SELECT tenantspersonal.tenantId, tenantName, houseName,roomName " +
            "FROM tenantspersonal,tablehouse , tablerooms " +
            "WHERE tenantspersonal.houseId = tablehouse.houseId " +
            "AND tenantspersonal.roomId = tablerooms.roomId " +
            "AND tenantspersonal.isRoomAlloted = :gotisRoomAlloted")
    LiveData<List<TenantNameHouseRoom>> getAllTenantNHR(boolean gotisRoomAlloted);

    /* Bills Fragment*/

    /* Bill entry fragment.
     * returns the selected tenant's :  tenantsPersonal object */
    @Query("SELECT roomId, meterPay, nonMeterPay, mFixedCharges FROM TenantsPersonal WHERE tenantId = :gotTenantId")
    LiveData<TenantBillEntry> getSelectedTenant(int gotTenantId);

    /* For creating new bill.*/
    @Insert
    void insertNewBill(Bills bills);

    @Update
    void updateBill(Bills bills);

    /* Update the Total bills*/
    @Query("UPDATE tenantspersonal SET totalBills = totalBills + :gotTotalbills WHERE tenantId = :gotTenantId")
    void updateTotalBillsinTenant(int gotTotalbills, int gotTenantId);

    /* Update the paid bills*/
    @Query("UPDATE tenantspersonal SET paidBills = paidBills + :gotPaidBills WHERE tenantId = :gotTenantId")
    void updatePaidBillsinTenant(int gotPaidBills, int gotTenantId);

    /* Getting to know if atleast one tenant is active*/
    @Query("SELECT isRoomAlloted FROM tenantspersonal WHERE isRoomAlloted = :getbolean LIMIT 1 ")
    LiveData<Boolean> getIsAnyTenantActive(boolean getbolean);

    /*Showing bills in recycle view of bills page*/
    @Transaction
    @Query("SELECT billId, bills.createDate, bills.monthlycharge, bills.additionalcharge, bills.totalAmt, " +
            "bills.electricCost, bills.isBillPaid, tenantspersonal.tenantName, tablehouse.houseName, tablerooms.roomName " +
            "FROM bills,tenantspersonal,tablehouse,tablerooms " +
            "WHERE bills.tenantId =  tenantspersonal.tenantId  " +
            "AND TenantsPersonal.roomId = tablerooms.roomId " +
            "AND TenantsPersonal.houseId = tableHouse.houseId " +
            "AND bills.isBillPaid = :gotBillPaid ORDER BY bills.createDate DESC")
    LiveData<List<BillItemForCard>> getAllBillForCard(boolean gotBillPaid);

    /* For entering meter reading.*/
    @Insert()
    void insertMeterReading(AllMetersData metersData);

    /* getting last meter reading based on the either house id , room id or meter id
     * logic of handling which querry to call is handled in repository.*/
    /* new date will be larger than the previous date.*/

    @Query("SELECT lastMeterReading FROM allmetersdata WHERE meterId = :givenMeterId ORDER BY date DESC LIMIT :noOfReadings ")
    LiveData<Long[]> getLastMeterEntry(long givenMeterId, int noOfReadings);

    @Query("SELECT allmetersdata.lastMeterReading "
            + "FROM allmetersdata , tablehouse" +
            " WHERE  tablehouse.meterid = allmetersdata.meterId AND tablehouse.houseId = :gotHouseid " +
            "ORDER BY allmetersdata.date DESC LIMIT :noOfreadings ")
    LiveData<Long[]> getLastMeterReadingForHouse(long gotHouseid, int noOfreadings);

    @Query("SELECT allmetersdata.lastMeterReading "
            + "FROM allmetersdata , tablerooms" +
            " WHERE tablerooms.meterId = allmetersdata.meterId" +
            " AND tablerooms.roomId = :gotRoomid " +
            "ORDER BY allmetersdata.date DESC LIMIT :noOfReadings ")
    LiveData<Long[]> getLastMeterReadingForRoom(int gotRoomid, int noOfReadings);

    /* get Meter no from room id*/
    @Query("SELECT meterid FROM tablerooms WHERE roomId = :gotRoomId")
    long getMeterId(int gotRoomId);

}
