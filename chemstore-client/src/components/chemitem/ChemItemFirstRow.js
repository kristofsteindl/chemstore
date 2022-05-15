import { Button, IconButton } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import useCollapse from "react-collapsed";
import { isExpired } from "../../utils/chem-item-utils";

const ChemItemFirstRow = props => {
    const { chemItem, setActiveModal, isManager, getToggleProps } = props

    const chemical = chemItem.chemical

    const getDeleteContent = () => {
        if (!isManager) {
            return <div></div>
        }
        return (
            <span  onClick={() => setActiveModal("DELETE")}>
                <li className="list-group-item action-button delete">              
                    <IconButton aria-label="delete" size="small">
                        <DeleteIcon fontSize="inherit"/>
                    </IconButton>
                </li>
            </span>
        )
    }

    const getOpenContent = chemItem => {
        if (chemItem.openingDate) {
            return (<div>{chemItem.openingDate}</div>)
        } else if (isExpired(chemItem)) {
            return (<div></div>)
        } else {
            return (
                <Button onClick={() => setActiveModal("OPEN")} className="btn action-button" variant="outlined" size="medium">
                    <i className="fa fa-edit">Open</i>
                </Button>
            )
        }
    }

    const getConsumeContent = chemItem => {
        if (!chemItem.openingDate && !isExpired(chemItem)) {
            return (<div></div>)
        } else if (chemItem.openingDate && !chemItem.consumptionDate) {
            return (
                <Button className="btn action-button" color="error" variant="outlined" size="medium" onClick={() => setActiveModal("CONSUME")}  >
                    <i className="fa fa-minus-circle">Consume</i>
                </Button>)
        } else {
            return (<div>{chemItem.consumptionDate}</div>)
        }
    }

    const getExpDate = chemItem => {
        if (chemItem.expirationDate) {
            return (<strong>{chemItem.expirationDate}</strong>)
        }
        return (<p>{chemItem.expirationDateBeforeOpened}</p>)
    }

    return (
        <div className="row"  {...getToggleProps()}>
            <div className="col-2">
                <h4 className="mx-auto">{chemical.shortName}</h4>
            </div>
            <div className="col-sm-1">
                <i>{chemItem.quantity} {chemItem.unit}</i>
            </div>
            <div className="col-1">
                <span className="mx-auto">{chemItem.arrivalDate}</span>
            </div>
            <div className="col-sm-2">
                <p>{chemItem.manufacturer.name}</p>
            </div>
            <div className="col-sm-2">
                <p>{chemItem.batchNumber}/{chemItem.seqNumber}</p>
            </div>
            <div className="col-sm-1">
                {getExpDate(chemItem)}
            </div>
            <div className="col-sm-1">
                {getOpenContent(chemItem)}
            </div>
            <div className="col-sm-1">
                {getConsumeContent(chemItem)}
            </div>
            <div className="col-sm-1">
                {getDeleteContent()}
            </div>
        </div>
    )
}

export default ChemItemFirstRow