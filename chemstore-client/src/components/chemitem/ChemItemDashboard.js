import React, { useEffect, useState } from 'react';
import Pagination from '../Pagination'
import ChemItem from './ChemItem'
import ChemItemHeader from './ChemItemHeader';
import "./ChemItemDashboard.css"
import { useSelector } from 'react-redux';
import { check } from '../../utils/securityUtils';
import axios from 'axios';
import RedirectFormButton from '../RedirectFormButton';

const PAGE_LIMIT = 20

function ChemItemDashboard() {
    
    const [onlyAvailable, setOnlyAvailable] = useState(true)
    const [chemItems, setChemItems] = useState([])
    const [currentPage, setCurrentPage] = useState(1)
    const [totalItems, setTotalItems] = useState(1)
    const setTotalPages = useState(0)[1]
    
    const toggleOnlyAvailable = () => {
        setOnlyAvailable(!onlyAvailable);
    };

    const loadChemItems = () => {
        check()
        onPageChanged({
            currentPage: currentPage,
            pageLimit: PAGE_LIMIT,
            onlyAvailable: onlyAvailable
        })
    }

    const selectedLab = useSelector((state) => state.selectedLab)
    const user = useSelector((state) => state.security.user)


    useEffect(() => {
        loadChemItems()
    }, [selectedLab, onlyAvailable])

    


    const deleteChemItem = async id => {
        await axios.delete(`/api/chem-item/${id}`)
        setChemItems(originalList => originalList.filter(chemItem => chemItem.id !== id))
    }

    const onPageChanged = data => {
        check()
        const { currentPage, pageLimit } = data;
        if (selectedLab && selectedLab.key && currentPage) {
            const availableString = onlyAvailable ? "&expired=false&consumed=false" : ""
            axios.get(`/api/chem-item/${selectedLab.key}?page=${currentPage - 1}&size=${pageLimit}${availableString}`)
                .then(result => {
                    setCurrentPage(currentPage)
                    setChemItems(result.data.content)
                    setTotalItems(result.data.totalItems)
                    setTotalPages(result.data.totalPages)
                })
        }
    }


    const getChemItemContent = (totalItems) => {
        if (!totalItems) {
            return <p className="lead"><i>There is no registered chemical for this lab so far</i></p>
        }
        return (
            <div className="container mb-5">
                <div className="row d-flex flex-row py-0">
                    <div className="w-100 px-4 py-0 d-flex flex-row flex-wrap align-items-center justify-content-between">
                        
                        <div className="d-flex flex-row py-2 align-items-center">
                            <Pagination 
                                totalRecords={totalItems}
                                pageLimit={PAGE_LIMIT} 
                                pageNeighbours={1}
                                onPageChanged={onPageChanged}
    
                            />
                            <div className="pad-chckbx" >
                                <input
                                    type="checkbox"
                                    checked={onlyAvailable}
                                    onChange={toggleOnlyAvailable}
                                />
                                <label className="pad-5" >Only available</label>
                                
                            </div>
                        
                        </div>
                    </div>
                
                    <ChemItemHeader />
                    <hr />
                    { chemItems.map(chemItem => 
                            <ChemItem
                                isManager={isManager}
                                key={chemItem.id}
                                chemItem={chemItem}
                                deleteChemItem={deleteChemItem}
                            
                            />
                        ) 
                    }
                </div>
            </div>
        )
    }

    const isManager =  (selectedLab.key) && selectedLab.labManagers.filter(manager => manager.username === user.username).length > 0
    const isAdmin = (selectedLab.key) && (user.labsAsAdmin.includes(selectedLab.value) || isManager)

    return (
        <div className="projects">
            <div className="container">
                <div className="row">
                    <div className="col-md-12">
                        <h3 className="display-4 text-center">Registered Chemicals</h3>
                        {isAdmin && 
                            <RedirectFormButton formRoute="/add-chem-item" buttonLabel="Register Chemical"/>
                        }
                        <hr />
                        {selectedLab.key ? 
                            getChemItemContent(totalItems) :
                            <p className="lead"><i>Please select a lab</i></p>
                        }

                    </div>
                </div>
            </div>
        </div>
    )
    
    
}

export default ChemItemDashboard
