import axios from "axios";
import jwt_decode from "jwt-decode";
import store from '../store';
import { SET_CURRENT_USER } from "../actions/types";

//TODO rename to 'setTokenToHeader
export const setJwt = token => {
    if (token) {
        axios.defaults.headers.common["Authorization"] = token;
    } else {
        delete axios.defaults.headers.common["Authorization"];
    }
}

export const logoutDispatch = () => dispatch => {
    localStorage.removeItem("jwt");
    setJwt(false);
    dispatch({
        type: SET_CURRENT_USER,
        payload: {}
    });
}


export const logout = () => {
    localStorage.removeItem("jwt");
    setJwt(false);
    store.dispatch({
        type: SET_CURRENT_USER,
        payload: {}
    });
}

export const refreshTokenAndUser = () => {
    const jwtToken = localStorage.jwt;
    if (jwtToken) {
      const decodedToken = jwt_decode(jwtToken);
      const currentTime = Date.now()/1000;
      if (decodedToken.exp < currentTime) {
        console.log("token expired");  
        logout();
        window.location.replace("/");
      } else {
        setJwt(jwtToken);
        store.dispatch({
            type: SET_CURRENT_USER,
            payload: decodedToken
        });
      }
    }
}

