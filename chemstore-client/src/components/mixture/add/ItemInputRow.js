import { TableCell, TableRow } from "@mui/material"
import axios from "axios"
import { useEffect, useState } from "react"
import Select from "react-dropdown-select"
import { useSelector } from "react-redux"

const ItemInputRow = props => {
    const { ingredientRow, type, amountFactor, selectedItems, setSelectedItems, setSpacer, posFromLast, creationDate } = props
    const ingredient = ingredientRow.ingredient
    const height = posFromLast * 50

    const isChemItems = (type === "CHEM_ITEM")

    const selectedLab = useSelector(state => state.selectedLab)

    const [ item, setItem ] = useState(selectedItems[ingredient.id])
    const [ items, setItems ] = useState([])
    const [ open, setOpen ] = useState(false)

    const fetchChemItems = () => {
        let query
        if (creationDate) {
            query = `/api/chem-item/${selectedLab.key}?chemicalId=${ingredient.id}&availableOn=${creationDate}&size=100`
        } else {
            query = `/api/chem-item/${selectedLab.key}?chemicalId=${ingredient.id}&opened=true&expired=false&consumed=false&size=100`
        }
        axios.get(query)
                .then(result => {setItems(result.data.content.map(item => (
                    {...item, label: `${item.manufacturer.name}, ${item.batchNumber}-${item.seqNumber}`}
                )))})
    }

    const fetchMixtures = () => {
        let query
        if (creationDate) {
            query = `/api/mixture/${selectedLab.key}?recipeId=${ingredient.id}&availableOn=${creationDate}&size=100`
        }
        else {
            query = `/api/mixture/${selectedLab.key}?recipeId=${ingredient.id}&available=false&size=100`
        }
        axios.get(query)
                .then(result => {setItems(result.data.content.map(mixture => (
                    {...mixture, label: `${mixture.id} (created at ${mixture.creationDate} by ${mixture.creator.fullName})`}
                )))})
    }

    useEffect(() => {
        if (selectedItems.length == 0) {
            setItem("")
        }
    }, [selectedItems])

    useEffect(() => {
        if (item) {
            const ingId = ingredient.id
            const newItem = {}
            newItem[ingId] = item
            setSelectedItems({...selectedItems, ...newItem})
        }

    }, [item])

    useEffect(() => {
        if (selectedLab) {
            if (isChemItems) {
                fetchChemItems()   
            } else {
                fetchMixtures()
            }
        }
        
    }, [selectedLab, creationDate])

    useEffect(() => {
        if (open) {
            const dropDownHeight = 15 + 30 * (items.length ? items.length : 1)
            if (dropDownHeight > height)
            setSpacer(dropDownHeight - height)
        } else {
            setSpacer(0)
        }
    }, [open])

    return (
        <TableRow key={ingredient.id} >
            <TableCell>{isChemItems ? ingredient.shortName : ingredient.name}</TableCell>
            <TableCell style={{width: "500px"}}>
                <Select
                    options={items}
                    values={items.filter(i => item && (i.id === item.id))}
                    onDropdownOpen={() => setOpen(true)}
                    onDropdownClose={() => setOpen(false)}
                    labelField="label"
                    valueField="id"
                    placeholder="item"
                    searchable={false}
                    clearable={false}
                    style={{height: "30px", width: "500px", fontSize: "12px", menuPortal: base => ({ ...base, zIndex: 9999 })}}
                    onChange={items => setItem(items[0] ? items[0] : "")}
                />
            </TableCell>
            <TableCell>{(amountFactor * (ingredientRow.amount)).toFixed(3)}</TableCell>
            <TableCell align="left"> {ingredientRow.unit}</TableCell>
        </TableRow>
    )

}

export default ItemInputRow