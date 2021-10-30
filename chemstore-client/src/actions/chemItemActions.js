import axios from "axios";
import { GET_ERRORS } from "./types";

export const createChemItem = (newChemItem, labKey, history) => async dispatch => {
    try {
        const res = await axios.post(`/api/chem-item/${labKey}`, newChemItem)
        history.push('/dashboard')
    } catch (err) {
        dispatch({
            type: GET_ERRORS,
            payload: err.response.data
        })
    }
}