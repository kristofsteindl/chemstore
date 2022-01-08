import { FILL_LAB_DROPDOWN, SELECT_LAB, SET_CURRENT_USER } from "../actions/types";

const initialState = {
    selectedLab: {}
}

export default function selectedLabReducer(state = initialState, action) {
    switch (action.type) {
        case FILL_LAB_DROPDOWN:
            return {
                ...state,
                labs: action.payload,
                labOptions: action.payload.map(lab => {return {id: lab.id, value: lab.key, label: lab.name}})
            }
        default: 
            return state
    }
}