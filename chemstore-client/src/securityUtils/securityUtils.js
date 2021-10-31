import axios from "axios";
import jwt_decode from "jwt-decode";
import store from '../store';
import { SET_CURRENT_USER } from "../actions/types";



export const setJwt = token => {
    if (token) {
        axios.defaults.headers.common["Authorization"] = token;
    } else {
        delete axios.defaults.headers.common["Authorization"];
    }
}

export const refreshTokenAndUser = () => {
    const jwtToken = localStorage.jwt;
    if (jwtToken) {
        setJwt(jwtToken);
        const decodedToken = jwt_decode(jwtToken);
        console.log(decodedToken)
        store.dispatch({
            type: SET_CURRENT_USER,
            payload: decodedToken
        });
        return decodedToken;
    }
}

export const logout = () => dispatch => {
    localStorage.removeItem("jwtToken");
    setJwt(false);
    dispatch({
        type: SET_CURRENT_USER,
        payload: {}
    });
}