package com.jaorcas.fightnet.models;

import android.widget.TableRow;

public class TableRowAttack {

    private TableRow tableRow;
    private Attack attack;

    public TableRowAttack(){

    }

    public TableRowAttack(TableRow tableRow, Attack attack) {
        this.tableRow = tableRow;
        this.attack = attack;
    }

    public TableRow getTableRow() {
        return tableRow;
    }

    public void setTableRow(TableRow tableRow) {
        this.tableRow = tableRow;
    }

    public Attack getAttack() {
        return attack;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }
}
