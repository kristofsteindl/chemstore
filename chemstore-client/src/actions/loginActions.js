import axios from "axios";
import { GET_ERRORS } from "./types";
import { refreshState, fetchLabs } from "../utils/securityUtils";


export const login = (loginRequest, history) => async dispatch => {
    try {
        const res = await axios.post('/api/login', loginRequest)
        const { token } = res.data
        localStorage.setItem("jwt", token)  
        refreshState();
        fetchLabs();
        history.push("/chem-items")
    } catch (error) {
        dispatch({
            type: GET_ERRORS,
            payload: error.response.data
        });   
    }
}