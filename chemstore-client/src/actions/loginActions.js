import axios from "axios";
import { GET_ERRORS } from "./types";
import { refreshTokenAndUser } from "../securityUtils/securityUtils";


export const login = (loginRequest, history) => async dispatch => {
    try {
        const res = await axios.post('/api/login', loginRequest)
        const { token } = res.data
        localStorage.setItem("jwt", token)  
        refreshTokenAndUser();
        history.push("/chem-items")
    } catch (error) {
        dispatch({
            type: GET_ERRORS,
            payload: error.response.data
        });   
    }
}