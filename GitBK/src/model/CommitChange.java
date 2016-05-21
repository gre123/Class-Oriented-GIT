/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * @author Grzesiek
 */
public class CommitChange {
    public int begin;
    public int end;
    public String changeCode;

    public CommitChange(int begin, int end, String changeCode) {
        this.begin = begin;
        this.end = end;
        this.changeCode = changeCode;
    }
}
