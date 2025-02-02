package com.example.function;

import java.util.function.Function;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import com.example.function.ResolveGitHubAccount.GitHubAccount;
import com.example.function.ResolveGitHubAccount.User;

@Component
@Description("名前をもとにしてGitHubのアカウント名を返す関数。")
public class ResolveGitHubAccount implements Function<User, GitHubAccount> {

    public record User(String name) {
    }

    public record GitHubAccount(String accountName) {
    }

    @Override
    public GitHubAccount apply(User user) {
        return new GitHubAccount(switch (user.name()) {
            case "うらがみ" -> "backpaper0";
            default -> "unknown";
        });
    }

}
