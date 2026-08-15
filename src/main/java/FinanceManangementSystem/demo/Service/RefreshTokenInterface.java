package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Model.RefreshToken;
import FinanceManangementSystem.demo.Model.User;

public interface RefreshTokenInterface {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyToken(RefreshToken token);
}
