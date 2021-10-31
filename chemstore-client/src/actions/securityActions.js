import axios from "axios";
import errorReducer from "../reducers/errorReducer";
import { GET_ERRORS } from "./types";
import { refreshTokenAndUser } from "../securityUtils/securityUtils";

export const createNewUser = (newUser, history) => async dispatch => {
    try {
        await axios.post("/api/account/user", newUser)
        history.push("/login")
    } catch (error) {
        dispatch({
            type:GET_ERRORS,
            payload: error.response.data
        });
    }
}

export const login = loginRequest => async dispatch => {
    try {
        const res = await axios.post('/api/login', loginRequest)
        const { token } = res.data
        localStorage.setItem("jwt", token)   
        refreshTokenAndUser();
    
    } catch (error) {
        dispatch({
            type: GET_ERRORS,
            payload: error.response.data
        });   
    }
}