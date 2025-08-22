import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Form, Button, Badge, Spinner, Image } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { hotelAPI } from '../services/api';
import { toast } from 'react-toastify';
import { FaSearch, FaMapMarkerAlt, FaStar, FaRupeeSign, FaImage } from 'react-icons/fa';

const SearchHotels = () => {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchFilters, setSearchFilters] = useState({
    searchType: 'all',
    searchValue: '',
    city: '',
    state: '',
    country: '',
    hotelName: '',
    maxAcCost: '',
    maxNonAcCost: ''
  });

  useEffect(() => {
    loadAllHotels();
  }, []);

  const loadAllHotels = async () => {
    setLoading(true);
    try {
      const response = await hotelAPI.getAllHotels();
      // Ensure we always set an array
      setHotels(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      toast.error('Failed to load hotels');
      console.error('Error loading hotels:', error);
      // Set empty array on error
      setHotels([]);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    setSearchFilters({
      ...searchFilters,
      [e.target.name]: e.target.value
    });
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      let response;
      const { searchType, city, state, country, hotelName, maxAcCost, maxNonAcCost } = searchFilters;

      switch (searchType) {
        case 'city':
          response = await hotelAPI.searchByCity(city);
          break;
        case 'state':
          response = await hotelAPI.searchByState(state);
          break;
        case 'country':
          response = await hotelAPI.searchByCountry(country);
          break;
        case 'name':
          response = await hotelAPI.searchByName(hotelName);
          break;
        case 'cityMaxAc':
          response = await hotelAPI.searchByCityAndMaxAcCost(city, maxAcCost);
          break;
        case 'cityMaxNonAc':
          response = await hotelAPI.searchByCityAndMaxNonAcCost(city, maxNonAcCost);
          break;
        default:
          response = await hotelAPI.getAllHotels();
      }

      setHotels(response.data);
      toast.success(`Found ${response.data.length} hotels`);
    } catch (error) {
      toast.error('Search failed');
      console.error('Search error:', error);
    } finally {
      setLoading(false);
    }
  };

  const renderSearchForm = () => {
    const { searchType } = searchFilters;

    return (
      <Card className="search-filters">
        <Card.Body>
          <h5 className="mb-3">
            <FaSearch className="me-2" />
            Search Hotels
          </h5>
          <Form onSubmit={handleSearch}>
            <Row>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Search Type</Form.Label>
                  <Form.Select
                    name="searchType"
                    value={searchType}
                    onChange={handleFilterChange}
                  >
                    <option value="all">All Hotels</option>
                    <option value="city">By City</option>
                    <option value="state">By State</option>
                    <option value="country">By Country</option>
                    <option value="name">By Hotel Name</option>
                    <option value="cityMaxAc">City + Max AC Cost</option>
                    <option value="cityMaxNonAc">City + Max Non-AC Cost</option>
                  </Form.Select>
                </Form.Group>
              </Col>

              {searchType === 'city' && (
                <Col md={4}>
                  <Form.Group className="mb-3">
                    <Form.Label>City</Form.Label>
                    <Form.Control
                      type="text"
                      name="city"
                      value={searchFilters.city}
                      onChange={handleFilterChange}
                      placeholder="Enter city name"
                      required
                    />
                  </Form.Group>
                </Col>
              )}

              {searchType === 'state' && (
                <Col md={4}>
                  <Form.Group className="mb-3">
                    <Form.Label>State</Form.Label>
                    <Form.Control
                      type="text"
                      name="state"
                      value={searchFilters.state}
                      onChange={handleFilterChange}
                      placeholder="Enter state name"
                      required
                    />
                  </Form.Group>
                </Col>
              )}

              {searchType === 'country' && (
                <Col md={4}>
                  <Form.Group className="mb-3">
                    <Form.Label>Country</Form.Label>
                    <Form.Control
                      type="text"
                      name="country"
                      value={searchFilters.country}
                      onChange={handleFilterChange}
                      placeholder="Enter country name"
                      required
                    />
                  </Form.Group>
                </Col>
              )}

              {searchType === 'name' && (
                <Col md={4}>
                  <Form.Group className="mb-3">
                    <Form.Label>Hotel Name</Form.Label>
                    <Form.Control
                      type="text"
                      name="hotelName"
                      value={searchFilters.hotelName}
                      onChange={handleFilterChange}
                      placeholder="Enter hotel name"
                      required
                    />
                  </Form.Group>
                </Col>
              )}

              {(searchType === 'cityMaxAc' || searchType === 'cityMaxNonAc') && (
                <>
                  <Col md={3}>
                    <Form.Group className="mb-3">
                      <Form.Label>City</Form.Label>
                      <Form.Control
                        type="text"
                        name="city"
                        value={searchFilters.city}
                        onChange={handleFilterChange}
                        placeholder="Enter city name"
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group className="mb-3">
                      <Form.Label>
                        Max {searchType === 'cityMaxAc' ? 'AC' : 'Non-AC'} Cost
                      </Form.Label>
                      <Form.Control
                        type="number"
                        name={searchType === 'cityMaxAc' ? 'maxAcCost' : 'maxNonAcCost'}
                        value={searchType === 'cityMaxAc' ? searchFilters.maxAcCost : searchFilters.maxNonAcCost}
                        onChange={handleFilterChange}
                        placeholder="Enter max cost"
                        required
                      />
                    </Form.Group>
                  </Col>
                </>
              )}

              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>&nbsp;</Form.Label>
                  <Button type="submit" variant="primary" disabled={loading} className="w-100 d-block">
                    {loading ? <Spinner animation="border" size="sm" /> : 'Search'}
                  </Button>
                </Form.Group>
              </Col>
            </Row>
          </Form>
        </Card.Body>
      </Card>
    );
  };

  const renderHotelCard = (hotel) => (
    <Col md={6} lg={4} key={hotel.hotelId} className="mb-4">
      <Card className="hotel-card h-100">
        {hotel.imagePath ? (
          <div style={{ height: '200px', overflow: 'hidden' }}>
            <Image 
              src={hotelAPI.getHotelImageUrl(hotel.hotelId)} 
              alt={hotel.hotelName}
              style={{ 
                width: '100%', 
                height: '100%', 
                objectFit: 'cover'
              }}
              onError={(e) => {
                e.target.style.display = 'none';
                e.target.nextSibling.style.display = 'flex';
              }}
            />
            <div 
              className="d-flex align-items-center justify-content-center bg-light text-muted"
              style={{ 
                height: '200px', 
                display: 'none',
                flexDirection: 'column'
              }}
            >
              <FaImage size={40} className="mb-2" />
              <small>No image available</small>
            </div>
          </div>
        ) : (
          <div 
            className="d-flex align-items-center justify-content-center bg-light text-muted"
            style={{ height: '200px', flexDirection: 'column' }}
          >
            <FaImage size={40} className="mb-2" />
            <small>No image available</small>
          </div>
        )}
        <Card.Body>
          <Card.Title>{hotel.hotelName}</Card.Title>
          <Card.Text>
            <FaMapMarkerAlt className="me-1 text-muted" />
            {hotel.city}, {hotel.state}, {hotel.country}
          </Card.Text>
          <Card.Text className="text-muted small">
            {hotel.description}
          </Card.Text>
          <div className="mb-2">
            <Badge bg="success" className="me-2">
              <FaStar className="me-1" />
              {hotel.avgRatingByCustomers || 'N/A'}
            </Badge>
          </div>
          <div className="mb-2">
            <small className="text-muted">
              <FaRupeeSign className="me-1" />
              AC: ₹{hotel.acRoomCost} | Non-AC: ₹{hotel.nonAcRoomCost}
            </small>
          </div>
          <div className="mb-3">
            {(() => {
              const totalAvailableRooms = (hotel.availableAcRooms || 0) + (hotel.availableNonAcRooms || 0);
              if (totalAvailableRooms === 0) {
                return (
                  <small className="text-muted">
                    <strong>Rooms:</strong> Fully booked
                  </small>
                );
              } else {
                return (
                  <small className="text-success">
                    <strong>Available Rooms:</strong> {hotel.availableAcRooms || 0} AC, {hotel.availableNonAcRooms || 0} Non-AC
                    <br />
                    <strong>Total: {totalAvailableRooms} rooms available</strong>
                  </small>
                );
              }
            })()}
          </div>
          {(() => {
            const totalAvailableRooms = (hotel.availableAcRooms || 0) + (hotel.availableNonAcRooms || 0);
            if (totalAvailableRooms === 0) {
              return (
                <Button variant="outline-secondary" disabled className="w-100">
                  View Details
                </Button>
              );
            } else {
              return (
                <Link to={`/hotel/${hotel.hotelId}`} className="btn btn-primary w-100">
                  View Details & Book
                </Link>
              );
            }
          })()}
        </Card.Body>
      </Card>
    </Col>
  );

  return (
    <Container className="py-4">
      <Row>
        <Col>
          <h2 className="mb-4">Search Hotels</h2>
          {renderSearchForm()}
        </Col>
      </Row>

      <Row className="mt-4">
        <Col>
          <h4>
            {Array.isArray(hotels) && hotels.length > 0 ? `Found ${hotels.length} hotels` : 'No hotels found'}
          </h4>
        </Col>
      </Row>

      {loading ? (
        <Row>
          <Col className="text-center py-5">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
          </Col>
        </Row>
      ) : (
        <Row>
          {Array.isArray(hotels) && hotels.length > 0 ? (
            hotels.map(renderHotelCard)
          ) : (
            <Col className="text-center py-5">
              <p className="text-muted">No hotels available at the moment.</p>
            </Col>
          )}
        </Row>
      )}
    </Container>
  );
};

export default SearchHotels;
