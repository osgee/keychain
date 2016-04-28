package com.superkeychain.keychain.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by taofeng on 3/24/16.
 */
public class AccountContent {

    public static List<Account> accounts = new ArrayList<>();
    public static Map<String, Account> accountsMap = new HashMap<>();

    private AccountContent(List<Account> accounts) {
        for (Account account : accounts) {
            addAccount(account);
        }
    }

    public static AccountContent newInstance(List<Account> accounts) {
        addAccounts(accounts);
        return new AccountContent(accounts);
    }

    public static void addAccounts(List<Account> accounts) {
        for (Account account : accounts) {
            addAccount(account);
        }
    }

    public static void setAccounts(List<Account> accounts) {
        accounts = new ArrayList<>();
        accountsMap = new HashMap<>();
        for (Account account : accounts) {
            addAccount(account);
        }
    }

    public static void addAccount(Account account) {
        accounts.add(account);
        accountsMap.put(account.getAccountId(), account);
    }

    public static Account getAccount(String id) {
        return accountsMap.get(id);
    }

    public static Account getAccount(int position) {
        if (position < accounts.size()) {
            return accounts.get(position);
        }
        return null;
    }
}
