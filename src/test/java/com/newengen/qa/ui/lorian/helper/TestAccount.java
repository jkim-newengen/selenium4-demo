package com.newengen.qa.ui.lorian.helper;

import lombok.Getter;

public enum TestAccount {
    DevTest("37"), F1800("67"), NotSelectedYet("");

    @Getter String accountId;

    TestAccount(String accountId) {
        this.accountId = accountId;
    }
}
